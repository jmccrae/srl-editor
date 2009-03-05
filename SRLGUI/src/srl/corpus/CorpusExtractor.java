/* 
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
*/
package srl.corpus;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import mccrae.tools.process.ProgressMonitor;
import mccrae.tools.process.StopSignal;
import mccrae.tools.strings.Strings;
import mccrae.tools.struct.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import srl.corpus.Corpus.QueryHit;
import srl.rule.Entity;
import srl.rule.Rule;
import srl.rule.RuleSet;
import srl.rule.SrlMatchRegion;

/**
 * @author John McCrae, National Institute of Informatics
 */
public class CorpusExtractor {

    private final Corpus corpus;

    public CorpusExtractor(Corpus corpus) {
        this.corpus = corpus;
    }
    
    /**
     * Tag the corpus
     * @param overlaps A collection, which this function will add any overlaps it detects to (an overlap is a pair of matches
     * where both matches hit the same token and both matches have one token not matched by the other e.g., [0,4] &amp; [1,5])
     * @param ruleSets The set of rules for named entity extraction
     */
    public void tagCorpus(Collection<RuleSet> ruleSets, Collection<Overlap> overlaps) throws IOException, CorpusConcurrencyException {
        tagCorpus(ruleSets, overlaps, null);
    }

     /**
     * Tag the corpus
     * @param overlaps A collection, which this function will add any overlaps it detects to (an overlap is a pair of matches
     * where both matches hit the same token and both matches have one token not matched by the other e.g., [0,4] &amp; [1,5])
     * @param ruleSets The set of rules for named entity extraction
     * @param monitor Monitors the progress surprisingly
     */
    public void tagCorpus(Collection<RuleSet> ruleSets, Collection<Overlap> overlaps, ProgressMonitor monitor) throws IOException, CorpusConcurrencyException {
        if (corpus.isIndexOpen()) {
            corpus.closeIndex();
        }
        final HashMap<String, List<HashMap<Entity, SrlMatchRegion>>> allMatches =
                new HashMap<String, List<HashMap<Entity, SrlMatchRegion>>>();
        int i = 0;
        for (RuleSet ruleSet : ruleSets) {
            int j = 0;
            for (final Pair<String, Rule> rulePair : ruleSet.rules) {
                if (monitor != null) {
                    monitor.setMessageVal("Matching rule " + rulePair.first);
                    monitor.setProgressVal((float) (i * ruleSet.rules.size() + j++) / (float) ruleSets.size() / (float) ruleSet.rules.size());
                }
                corpus.query(rulePair.second.getCorpusQuery(), new QueryHit() {

                    public void hit(Document d, StopSignal signal) {
                        String name = d.getField("name").stringValue();
                        if (allMatches.get(name) == null) {
                            allMatches.put(name, new LinkedList<HashMap<Entity, SrlMatchRegion>>());
                        }
                        allMatches.get(name).addAll(rulePair.second.getMatch(new SrlDocument(d, corpus.processor, false), false));
                    }
                });
            }
            i++;
        }
        long lockID = corpus.reopenIndex(true);
        IndexReader reader = null;
        try {
            reader = IndexReader.open(corpus.indexWriter.getDirectory());
            i = 0;
            for (Map.Entry<String, List<HashMap<Entity, SrlMatchRegion>>> entry : allMatches.entrySet()) {
                Vector<Pair<Entity, SrlMatchRegion>> matches = findOverlapsAndKill(entry.getValue(), overlaps);
                addTagsToDocument(entry.getKey(), matches, reader, monitor);
                if (monitor != null) {
                    monitor.setMessageVal("Updating document " + entry.getKey());
                    monitor.setProgressVal((float) i++ / allMatches.size());
                }
            }
        } finally {
            corpus.closeIndex(lockID);
        }
        corpus.optimizeIndex();
        if (reader != null) {
            reader.close();
        }
        if (monitor != null) {
            monitor.setMessageVal("Corpus tagging complete");
            monitor.setProgressVal(1.0f);
        }
    }
    
    /** Apply the tagging algorithm.
     * @param sents The document as a list of sentences
     * @param ruleSets The rulesets to apply
     * @param p The linguistic processor
     * @return The document as a list (tags are added as BeginTagToken and 
     * EndTagToken objects
     */
    public static List<SrlDocument> tagSentences(List<SrlDocument> sents, Collection<RuleSet> ruleSets, Processor p) throws IOException {
        final Vector<List<HashMap<Entity, SrlMatchRegion>>> allMatches =
                new Vector<List<HashMap<Entity, SrlMatchRegion>>>(sents.size());
        
        for (Collection<org.apache.lucene.analysis.Token> sent : sents) {
            allMatches.add(new LinkedList<HashMap<Entity, SrlMatchRegion>>());
        }
        for (RuleSet ruleSet : ruleSets) {
            for (Pair<String, Rule> rulePair : ruleSet.rules) {
                int i = 0;
                for (SrlDocument sent : sents) {
                    allMatches.get(i++).addAll(rulePair.second.getMatch(sent, false));
                }
            }
        }
        List<SrlDocument> rval = new Vector<SrlDocument>(sents.size());
        int i = 0;
        for (List<HashMap<Entity, SrlMatchRegion>> matches : allMatches) {
            // TODO: Work out how to call findOverlapsAndKill
            rval.add(new SrlDocument("name", addEntities((SrlDocument) sents.get(i++), sortMatches(matches)), p));
        }
        return rval;
    }
   
    private static Vector<Pair<Entity, SrlMatchRegion>> findOverlapsAndKill(List<HashMap<Entity, SrlMatchRegion>> allMatches,
            Collection<Overlap> overlaps) {
        Vector<Pair<Entity, SrlMatchRegion>> matches = sortMatches(allMatches);
        ListIterator<Pair<Entity, SrlMatchRegion>> mIter = matches.listIterator(matches.size());
        LOOP:
        while (mIter.hasPrevious()) {
            Pair<Entity, SrlMatchRegion> m1 = mIter.previous();
            ListIterator<Pair<Entity, SrlMatchRegion>> mIter2 = matches.listIterator();
            while (mIter2.hasNext()) {
                Pair<Entity, SrlMatchRegion> m2 = mIter2.next();
                if (m2.second.beginRegion < m1.second.beginRegion &&
                        m2.second.endRegion < m1.second.endRegion &&
                        m2.second.beginRegion < m1.second.endRegion &&
                        m2.second.endRegion > m1.second.beginRegion) {
                    mIter2.remove();
                    mIter = matches.listIterator(matches.size());
                    if (overlaps != null) {
                        overlaps.add(new Overlap(m1, m2));
                    }
                    continue LOOP;
                } else if (m2.second.beginRegion == m1.second.beginRegion &&
                        m1.second.endRegion == m2.second.beginRegion &&
                        m1.first.entityType.equals(m2.first.entityType) &&
                        m1.first.entityValue.equals(m2.first.entityValue)) {
                    mIter2.remove();
                    mIter = matches.listIterator(matches.size());
                    continue LOOP;
                }

            }
        }
        return matches;
    }

    
    /** Used to represent an overlap in tagging */
    public static class Overlap {

        public Entity e1,  e2;
        public SrlMatchRegion r1,  r2;

        public Overlap(Pair<Entity, SrlMatchRegion> m1, Pair<Entity, SrlMatchRegion> m2) {
            e1 = m1.first;
            e2 = m2.first;
            r1 = m1.second;
            r2 = m2.second;
        }
    }

    
    /** Sort a selection of matches in order of appearance */
    public static Vector<Pair<Entity, SrlMatchRegion>> sortMatches(List<HashMap<Entity, SrlMatchRegion>> matches) {
        Vector<Pair<Entity, SrlMatchRegion>> rv = new Vector<Pair<Entity, SrlMatchRegion>>(matches.size());
        for (HashMap<Entity, SrlMatchRegion> match : matches) {
            for (Map.Entry<Entity, SrlMatchRegion> entry : match.entrySet()) {
                rv.add(new Pair<Entity, SrlMatchRegion>(entry.getKey(), entry.getValue()));
            }
        }
        Collections.sort(rv, new Comparator() {

            public int compare(Object o1, Object o2) {
                Pair<Entity, SrlMatchRegion> m1 = (Pair<Entity, SrlMatchRegion>) o1, m2 = (Pair<Entity, SrlMatchRegion>) o2;
                if (m1.second.endRegion < m2.second.endRegion) {
                    return -1;
                } else if (m1.second.endRegion > m2.second.endRegion) {
                    return 1;
                } else if (m1.second.beginRegion < m2.second.beginRegion) {
                    return -1;
                } else if (m2.second.beginRegion > m2.second.endRegion) {
                    return 1;
                } else {
                    return m1.first.entityType.compareTo(m2.first.entityType);
                }
            }
        });
        Iterator<Pair<Entity, SrlMatchRegion>> matchIter = rv.iterator();
        if (!matchIter.hasNext()) {
            return rv;
        }
        Pair<Entity, SrlMatchRegion> last = matchIter.next();
        while (matchIter.hasNext()) {
            Pair<Entity, SrlMatchRegion> next = matchIter.next();
            if (next.first == last.first && next.second.beginRegion == last.second.beginRegion && next.second.endRegion == last.second.endRegion) {
                matchIter.remove();
            } else {
                last = next;
            }
        }
        return rv;
    }


    
     /**
     * (expert) Add a set of tags from an external source. 
     * @param docName The document to add the tags to
     * @param matches The matches (formatted as if it was the result
     * @throws java.io.IOException
     * @throws org.apache.lucene.index.CorruptIndexException
     */
    public void addTagsToDocument(String docName, List<Vector<Pair<Entity, SrlMatchRegion>>> matches) throws IOException, CorruptIndexException, CorpusConcurrencyException {
        corpus.reopenIndex();
        IndexReader reader = IndexReader.open(corpus.indexWriter.getDirectory());
        int i = 0;
        for (Vector<Pair<Entity, SrlMatchRegion>> match : matches) {
            addTagsToDocument(docName + " " + i, match, reader, null);
            i++;
        }
        corpus.closeIndex();
        reader.close();
    }

    private void addTagsToDocument(String docName, Vector<Pair<Entity, SrlMatchRegion>> matches, IndexReader reader, ProgressMonitor monitor)
            throws IOException, CorruptIndexException {
        String docNameProper = docName.toLowerCase().split(" ")[0];
        Term t = new Term("name", docNameProper);
        int docNo = Integer.parseInt(docName.split(" ")[1]);
        TermDocs td = reader.termDocs(t);
        Document old;
        while (true) {
            if (!td.next()) {
                throw new RuntimeException("Lost document: " + docName);
            }
            old = reader.document(td.doc());
            String dn = old.getField("name").stringValue();
            String[] ss = dn.split(" ");
            if (dn.matches(".* .*") && ss[0].equals(docNameProper) &&
                    Integer.parseInt(ss[1]) == docNo) {
                break;
            }
        }

        Document newDoc = new Document();
        newDoc.add(new Field("name", old.getField("name").stringValue(), Field.Store.YES, Field.Index.TOKENIZED));
        newDoc.add(new Field("contents", old.getField("contents").stringValue(), Field.Store.YES, Field.Index.TOKENIZED));
        newDoc.add(new Field("uid", old.getField("uid").stringValue(), Field.Store.YES, Field.Index.TOKENIZED));
        String taggedContents = addEntities(new SrlDocument(old, corpus.processor, false), matches);
        newDoc.add(new Field("taggedContents", taggedContents, Field.Store.YES, Field.Index.TOKENIZED));
        Term uidT = new Term("uid", old.getField("uid").stringValue());
        corpus.indexWriter.updateDocument(uidT, newDoc);
    }

        /**
     * Add Named Entity Tags
     */
    private static String addEntities(SrlDocument sentence, Vector<Pair<Entity, SrlMatchRegion>> matches) {
        List<String> tokens = new LinkedList<String>();
        for (org.apache.lucene.analysis.Token tk : sentence) {
            if(tk instanceof EndTagToken) {
                tokens.add(((EndTagToken)tk).getTag());
            } else if(tk instanceof BeginTagToken) {
                tokens.add(((BeginTagToken)tk).getTag());
            } else {    
                tokens.add(tk.termText());
            }
        }
        List<List<String>> begins = new LinkedList<List<String>>();
        List<List<String>> ends = new LinkedList<List<String>>();
        for (int i = 0; i <= tokens.size() + 1; i++) {
            ends.add(new LinkedList<String>());
            begins.add(new LinkedList<String>());
        }
        ends.add(new LinkedList<String>());
        for (Pair<Entity, SrlMatchRegion> entry : matches) {
            begins.get(entry.second.beginRegion).add(0, "<" +
                    entry.first.entityType + " cl=\"" +
                    entry.first.entityValue + "\">");
            try {
                ends.get(entry.second.endRegion).add("</" +
                        entry.first.entityType + ">");
            } catch (IndexOutOfBoundsException x) {
                System.out.println(sentence.getName());
                x.printStackTrace();
            }
        }
        int offset = 0;
        for (int i = 0; i < ends.size(); i++) {
            for (String s : ends.get(i)) {
                tokens.add(i + offset++, s);
            }
            if (i < begins.size()) {
                for (String s : begins.get(i)) {
                    tokens.add(i + offset++, s);
                }
            }
        }
        return Strings.join(" ", tokens);
    }

    
    /** Extract all the templates from this corpus */
    public void extractTemplates(Collection<RuleSet> ruleSets) throws IOException, CorpusConcurrencyException {
        extractTemplates(ruleSets, null);
    }

    /** Extract all the templates from this corpus */
    public void extractTemplates(Collection<RuleSet> ruleSets, ProgressMonitor monitor) throws IOException, CorpusConcurrencyException {
        if (corpus.isIndexOpen()) {
            corpus.closeIndex();
        }
        final HashMap<String, List<String>> allMatches =
                new HashMap<String, List<String>>();
        int i = 0;
        for (RuleSet ruleSet : ruleSets) {
            int j = 0;
            for (final Pair<String, Rule> rulePair : ruleSet.rules) {
                if (monitor != null) {
                    monitor.setMessageVal("Matching rule " + rulePair.first);
                    monitor.setProgressVal((float) (i * ruleSet.rules.size() + j) / (float) ruleSets.size() / (float) ruleSet.rules.size());
                }
                corpus.query(rulePair.second.getCorpusQuery(), new QueryHit() {

                    public void hit(Document d, StopSignal signal) {
                        String name = d.getField("uid").stringValue();
                        if (allMatches.get(name) == null) {
                            allMatches.put(name, new LinkedList<String>());
                        }
                        List<String> heads = rulePair.second.getHeads(new SrlDocument(d, corpus.processor, true));
                        if(!heads.isEmpty())
                            allMatches.get(name).add(Strings.join(";", heads));
                    }
                });
            }
            i++;
        }
        long lockID = corpus.reopenIndex(true);
        IndexReader reader = null;
        try {
            reader = IndexReader.open(corpus.indexWriter.getDirectory());
            i = 0;
            for (Map.Entry<String, List<String>> entry : allMatches.entrySet()) {

                TermDocs td = reader.termDocs(new Term("uid", entry.getKey()));
                if (!td.next()) {
                    throw new RuntimeException("Lost Document!");
                }
                Document d = reader.document(td.doc());
                if (monitor != null) {
                    monitor.setMessageVal("Updating document " + d.getField("name").stringValue());
                    monitor.setProgressVal((float) i++ / allMatches.size());
                }
                d.removeFields("extracted");
                d.add(new Field("extracted", Strings.join("\n", entry.getValue()), Field.Store.YES, Field.Index.NO));
                corpus.indexWriter.updateDocument(new Term("uid", entry.getKey()), d);
            }
        } finally {
            reader.close();
            corpus.closeIndex(lockID);
        }
        corpus.optimizeIndex();
        if (monitor != null) {
            monitor.setMessageVal("Template Extraction complete");
            monitor.setProgressVal(1.0f);
        }
    }

}