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
package srl.gui;

import java.awt.*;
import java.awt.event.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.tree.*;
import mccrae.tools.struct.ListenableSet;
import srl.corpus.Corpus;
import srl.corpus.CorpusConcurrencyException;
import srl.corpus.CorpusExtractor;
import srl.project.SrlProject;
import srl.rule.*;
import srl.wordlist.WordListSet;
import srl.wordlist.WordListEntry;

/**
 * The application's main frame.
 */
public class SRLGUIView extends FrameView {

    public SRLGUIView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        ruleSetIcon = resourceMap.getIcon("srl.ruleSetIcon");
        wordListIcon = resourceMap.getIcon("srl.wordListIcon");
        corpusIcon = resourceMap.getIcon("srl.corpusIcon");
        closeTabIcon = resourceMap.getIcon("srl.closeTabIcon");
        searchIcon = resourceMap.getIcon("srl.searchTabIcon");

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SRLGUIApp.getApplication().getMainFrame();
            aboutBox = new SRLGUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SRLGUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainTree = new javax.swing.JTree();
        rightPane = new javax.swing.JTabbedPane();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButton9 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        mainTree.setEnabled(false);
        mainTree.setModel(new DefaultTreeModel(SRLGUIApp.getApplication().getMainTreeNode()));
        mainTree.setName("mainTree"); // NOI18N
        mainTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mainTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainTreeMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(mainTree);

        rightPane.setEnabled(false);
        rightPane.setName("rightPane"); // NOI18N

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getActionMap(SRLGUIView.class, this);
        jButton1.setAction(actionMap.get("newProject")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(srl.gui.SRLGUIApp.class).getContext().getResourceMap(SRLGUIView.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(28, 28));
        jToolBar1.add(jButton1);

        jButton2.setAction(actionMap.get("openProject")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(28, 28));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jButton3.setAction(actionMap.get("saveProject")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setEnabled(false);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setPreferredSize(new java.awt.Dimension(28, 28));
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar1.add(jSeparator3);

        jButton4.setAction(actionMap.get("addRuleSet")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setEnabled(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setPreferredSize(new java.awt.Dimension(28, 28));
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton5.setAction(actionMap.get("addWordList")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setEnabled(false);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setPreferredSize(new java.awt.Dimension(28, 28));
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);

        jButton6.setAction(actionMap.get("addCorpusDoc")); // NOI18N
        jButton6.setEnabled(false);
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setPreferredSize(new java.awt.Dimension(28, 28));
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jToolBar1.add(jSeparator4);

        jButton7.setAction(actionMap.get("tagCorpus")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setEnabled(false);
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton7);

        jButton8.setAction(actionMap.get("extractTemplates")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setEnabled(false);
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton8);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        jButton9.setAction(actionMap.get("searchCorpus")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setEnabled(false);
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton9);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(rightPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rightPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newProjectMenuItem.setAction(actionMap.get("newProject")); // NOI18N
        newProjectMenuItem.setText(resourceMap.getString("newProjectMenuItem.text")); // NOI18N
        newProjectMenuItem.setName("newProjectMenuItem"); // NOI18N
        fileMenu.add(newProjectMenuItem);

        jMenuItem2.setAction(actionMap.get("openProject")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        fileMenu.add(jMenuItem2);

        jMenuItem1.setAction(actionMap.get("saveProject")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setEnabled(false);
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setEnabled(false);
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem3.setAction(actionMap.get("addRuleSet")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAction(actionMap.get("addWordList")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAction(actionMap.get("addCorpusDoc")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenu1.add(jMenuItem5);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu1.add(jSeparator1);

        jMenuItem6.setAction(actionMap.get("tagCorpus")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenu1.add(jMenuItem6);

        jMenuItem7.setAction(actionMap.get("extractTemplates")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenu1.add(jMenuItem7);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jMenu1.add(jSeparator5);

        jMenuItem8.setAction(actionMap.get("searchCorpus")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu1.add(jMenuItem8);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jMenu1.add(jSeparator8);

        jMenuItem12.setAction(actionMap.get("importRuleSet")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenu1.add(jMenuItem12);

        jMenuItem13.setAction(actionMap.get("importWordList")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenu1.add(jMenuItem13);

        jMenuItem14.setAction(actionMap.get("importTagged")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenu1.add(jMenuItem14);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jMenu1.add(jSeparator9);

        jMenuItem11.setAction(actionMap.get("writeTagged")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenu1.add(jMenuItem11);

        jMenuItem10.setAction(actionMap.get("writeTemplates")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu1.add(jMenuItem10);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem9.setAction(actionMap.get("openWiki")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        helpMenu.add(jMenuItem9);

        jSeparator7.setName("jSeparator7"); // NOI18N
        helpMenu.add(jSeparator7);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 556, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    private void mainTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMousePressed
        if (!mainTree.isEnabled()) {
            return;
        }
        if (evt.getClickCount() == 2 && !evt.isPopupTrigger()) {
            final TreePath path = mainTree.getPathForLocation(evt.getX(), evt.getY());
            if (path == null || path.getPath() == null) {
                return;
            }
            if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules")) {
                String patternSetName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openRuleSetPane(patternSetName, Rule.TEMPLATE_RULE);
            } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules")) {
                String patternSetName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openRuleSetPane(patternSetName, Rule.ENTITY_RULE);
            } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
                String wordListName = (String) ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject();
                openWordListPane(wordListName);
            } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Corpus")) {
                openCorpusPane();
            }
        } else if (evt.isPopupTrigger()) {
            onMainTreePopupTrigger(evt);
        }
    }//GEN-LAST:event_mainTreeMousePressed

private void mainTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMouseReleased
        if(mainTree.isEnabled() && evt.isPopupTrigger()) {
            onMainTreePopupTrigger(evt);
        }
}//GEN-LAST:event_mainTreeMouseReleased

    private void onMainTreePopupTrigger(java.awt.event.MouseEvent evt) {
        final TreePath path = mainTree.getPathForLocation(evt.getX(), evt.getY());
        if (path == null || path.getPath() == null) {
            return;
        }
        if (path.getPath().length == 3 && (((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules") ||
                ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules"))) {
            final int ruleType = ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules") ? Rule.ENTITY_RULE : Rule.TEMPLATE_RULE;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add rule set") {

                public void actionPerformed(ActionEvent e) {
                    addRuleSet(ruleType);
                }
            });
            menu.add(addItem);
            JMenuItem removeItem = new JMenuItem();
            final String name = ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject().toString();
            removeItem.setAction(new AbstractAction("Remove rule set \"" + name + "\"") {

                public void actionPerformed(ActionEvent e) {
                    removeRuleSet(ruleType, name,
                            (DefaultMutableTreeNode) path.getLastPathComponent());
                }
            });
            menu.add(removeItem);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open \"" + name + "\" pane") {

                public void actionPerformed(ActionEvent e) {
                    openRuleSetPane(name, ruleType);
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Corpus")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem reInit = new JMenuItem();
            reInit.setAction(new AbstractAction("Re-initialize") {

                public void actionPerformed(ActionEvent e) {
                    Thread t = new Thread(new Runnable() {

                        public void run() {
                            try {
                                SRLGUIApp.getApplication().proj.corpus.resupport();
                                JOptionPane.showMessageDialog(getFrame(), "Corpus re-initialized", "Corpus", JOptionPane.INFORMATION_MESSAGE);
                                SRLGUIApp.getApplication().setModified();
                            } catch (IOException x) {
                                error(x, "Cannot re-initialize corpus");
                            } catch(CorpusConcurrencyException x) {
                                error(x, "Corpus locked");
                            }
                        }
                    });
                    t.start();
                }
            });
            menu.add(reInit);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open corpus pane") {

                public void actionPerformed(ActionEvent e) {
                    openCorpusPane();
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 3 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add word list") {

                public void actionPerformed(ActionEvent e) {
                    addWordList();
                }
            });
            menu.add(addItem);
            JMenuItem removeItem = new JMenuItem();
            final String name = ((DefaultMutableTreeNode) path.getPath()[2]).getUserObject().toString();
            removeItem.setAction(new AbstractAction("Remove word list \"" + name + "\"") {

                public void actionPerformed(ActionEvent e) {
                    removeWordList((DefaultMutableTreeNode) path.getLastPathComponent());
                }
            });
            menu.add(removeItem);
            JMenuItem openItem = new JMenuItem();
            openItem.setAction(new AbstractAction("Open \"" + name + "\" pane") {

                public void actionPerformed(ActionEvent e) {
                    openWordListPane(name);
                }
            });
            menu.add(openItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Word List Sets")) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add word list") {

                public void actionPerformed(ActionEvent e) {
                    addWordList();
                }
            });
            menu.add(addItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        } else if (path.getPath().length == 2 && (((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Template Rules") ||
                ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules"))) {
            final int ruleType = ((DefaultMutableTreeNode) path.getPath()[1]).getUserObject().equals("Entity Rules") ? Rule.ENTITY_RULE : Rule.TEMPLATE_RULE;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem addItem = new JMenuItem();
            addItem.setAction(new AbstractAction("Add rule set") {

                public void actionPerformed(ActionEvent e) {
                    addRuleSet(ruleType);
                }
            });
            menu.add(addItem);
            menu.show(mainTree, evt.getX(), evt.getY());
        }

    }

    private void openCorpusPane() {
        JPanel p = getPanel(SRLGUIApp.SRL_CORPUS, "");
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            Component c = new CorpusDocumentPanel();
            rightPane.addTab("Corpus", corpusIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_CORPUS, "Corpus",corpusIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    private void openWordListPane(String wordListName) {
        JPanel p = getPanel(SRLGUIApp.SRL_WORDLIST, wordListName);
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            Component c = new WordListPanel(SRLGUIApp.getApplication().wordLists.get(wordListName));
            rightPane.addTab(wordListName, wordListIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_WORDLIST, wordListName,wordListIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    private class CloseTabIcon implements Icon {
 
	private final Icon icon;
	private JTabbedPane tabbedPane = null;
	private transient Rectangle position = null;
 
	/**
	 * Creates a new instance of CloseTabIcon.
	 */
	public CloseTabIcon() {
		this.icon = closeTabIcon;
	}
 
	/**
	 * when painting, remember last position painted so we can see if the user clicked on the icon.
	 */
	public void paintIcon(Component component, Graphics g, int x, int y) {
 
		// Lazily create a link to the owning JTabbedPane and attach a listener to it, so clicks on the
		// selector tab can be intercepted by this code.
		if (tabbedPane == null) {
			tabbedPane = (JTabbedPane) component;
 
			tabbedPane.addMouseListener(new MouseAdapter() {
 
				@Override
				public void mouseReleased(MouseEvent e) {
					// asking for isConsumed is *very* important, otherwise more than one tab might get closed!
					if (! e.isConsumed() && position.contains(e.getX(), e.getY())) {
						Component p = tabbedPane.getSelectedComponent();
						if (p instanceof Closeable) {
                                                    if (!((Closeable) p).onClose()) {
                                                        e.consume();
                                                        return;
                                                    }
                                                }
                                                rightPane.remove(p);
						e.consume();
					}
				}
			});
		}
 
		position = new Rectangle(x, y, getIconWidth(), getIconHeight());
		icon.paintIcon(component, g, x, y);
	}
 
	/**
	 * just delegate
	 */
	public int getIconWidth() {
		return icon.getIconWidth();
	}
 
	/**
	 * just delegate
	 */
	public int getIconHeight() {
		return icon.getIconHeight();
	}
 
    }
    
    private void openRuleSetPane(String ruleSetName, int ruleType) {
        JPanel p = getPanel(ruleType + 1, ruleSetName);
        if (p != null) {
            rightPane.setSelectedComponent(p);
        } else {
            RuleSet rs;
            if (ruleType == Rule.ENTITY_RULE) {
                rs = SRLGUIApp.getApplication().entityRuleSets.get(ruleSetName);
            } else {
                rs = SRLGUIApp.getApplication().templateRuleSets.get(ruleSetName);
            }
            Component c = new RuleSetPanel(rs);
            rightPane.addTab(ruleSetName, ruleSetIcon, c);
            try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(ruleType + 1, ruleSetName,ruleSetIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }
    
    void openShowDocPane(String docName, TreeSet<DocHighlight> highlights, int mode, String msg) {
        ShowDocPanel p = new ShowDocPanel(docName, highlights, mode, msg);
        rightPane.addTab(docName + " (" + msg + ")", p);
        try {
            rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_SHOW_DOC, p.name,corpusIcon));
        } catch(NoSuchMethodError e) {
            // Java 1.5 compatibility
            rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
        }
        rightPane.setSelectedComponent(p);
    }

    private void error(Exception x, String title) {
        x.printStackTrace();
        JOptionPane.showMessageDialog(getFrame(), x.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public JPanel getPanel(int type, String name) {
        for (int i = 0; i < rightPane.getTabCount(); i++) {
            Component c = rightPane.getComponentAt(i);
            switch (type) {
                case SRLGUIApp.SRL_ENTITY_RULESET:
                    if (c instanceof RuleSetPanel && ((RuleSetPanel) c).ruleSet.name.equals(name) && ((RuleSetPanel) c).ruleSet.ruleType == Rule.ENTITY_RULE) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_TEMPLATE_RULESET:
                    if (c instanceof RuleSetPanel && ((RuleSetPanel) c).ruleSet.name.equals(name) && ((RuleSetPanel) c).ruleSet.ruleType == Rule.TEMPLATE_RULE) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_WORDLIST:
                    if (c instanceof WordListPanel && ((WordListPanel) c).wl.name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_CORPUS:
                    if (c instanceof CorpusDocumentPanel) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_PROJECT:
                    if (c instanceof ProjectPanel) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_SEARCH:
                    if (c instanceof SearchPanel && ((SearchPanel)c).name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                case SRLGUIApp.SRL_SHOW_DOC:
                    if (c instanceof ShowDocPanel && ((ShowDocPanel)c).name.equals(name)) {
                        return (JPanel) c;
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return null;
    }

    public void closeTab(int type, String name) {
        JPanel p = getPanel(type, name);
        if (p instanceof Closeable) {
            if (!((Closeable) p).onClose()) {
                return;
            }
        }
        rightPane.remove(p);
    }
    JFileChooser jfc;

    @Action
    public void openPatternFile() {
        String[] opts = {"Entity", "Template"};
        int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
        if (ruleType == JOptionPane.CLOSED_OPTION) {
            return;
        }
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            RuleSet ps;
            try {
                ps = RuleSet.loadFromFile(jfc.getSelectedFile(), ruleType);
            } catch (Exception x) {
                JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not open file", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ruleType == Rule.ENTITY_RULE) {
                SRLGUIApp.getApplication().entityRuleSets.put(ps.name, ps);
            } else {
                SRLGUIApp.getApplication().templateRuleSets.put(ps.name, ps);
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(ps.name);
            DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                    ruleSet,
                    ruleSet.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));

        }
    }

    @Action
    public void newProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            int option = JOptionPane.showConfirmDialog(SRLGUIApp.getApplication().getMainFrame(),
                    "Current project has been modified, do you want save?", "SRL Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
                } catch (IOException x) {
                    x.printStackTrace();
                    JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (CorpusConcurrencyException x) {
                    x.printStackTrace();
                    JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        NewProjectDialog dial = new NewProjectDialog(this.getFrame(), true);
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        if (jfc.getSelectedFile() != null) {
            dial.setDefaultFile(jfc.getSelectedFile().getPath());
        }
        dial.setVisible(true);
        if (dial.returnVal) {
            try {
                proj = new SrlProject(dial.getPath(), dial.getProcessor());
            } catch (Exception x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not create project", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SRLGUIApp.getApplication().proj = proj;
            reloadProject();
        }
    }

    private void reloadProject() {
        DefaultTreeModel dtm = (DefaultTreeModel) mainTree.getModel();
        DefaultMutableTreeNode entityRules = SRLGUIApp.getApplication().entityRules;
        DefaultMutableTreeNode templateRules = SRLGUIApp.getApplication().templateRules;
        SRLGUIApp a = SRLGUIApp.getApplication();
        while (entityRules.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) entityRules.getChildAt(0));
        }
        while (templateRules.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) templateRules.getChildAt(0));
        }
        DefaultMutableTreeNode wordlists = SRLGUIApp.getApplication().wordList;
        a.wordLists.clear();
        while (wordlists.getChildCount() > 0) {
            dtm.removeNodeFromParent((MutableTreeNode) wordlists.getChildAt(0));
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        a.templateRuleSets.clear();
        a.entityRuleSets.clear();
        if (proj != null) {
            for (WordListSet wl : proj.wordlists) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(wl.name), wordlists, wordlists.getChildCount());
                a.wordLists.put(wl.name, wl);
            }
            for (RuleSet rs : proj.entityRulesets) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(rs.name), entityRules, entityRules.getChildCount());
                a.entityRuleSets.put(rs.name, rs);
            }
            for (RuleSet rs : proj.templateRulesets) {
                dtm.insertNodeInto(new DefaultMutableTreeNode(rs.name), templateRules, templateRules.getChildCount());
                a.templateRuleSets.put(rs.name, rs);
            }
        }
        rightPane.removeAll();
        mainTree.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        jButton8.setEnabled(true);
        jButton9.setEnabled(true);
        jMenu1.setEnabled(true);
        rightPane.setEnabled(true);
        rightPane.add(new ProjectPanel(proj));
    }

    @Action
    public void openProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            int option = JOptionPane.showConfirmDialog(SRLGUIApp.getApplication().getMainFrame(),
                    "Current project has been modified, do you want save?", "SRL Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
                } catch (IOException x) {
                    x.printStackTrace();
                    JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (CorpusConcurrencyException x) {
                    x.printStackTrace();
                    JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        try {
            if(proj != null)
                proj.corpus.closeCorpus(); 
        } catch(IOException x) {
            x.printStackTrace();
            error(x, "Cannot close project");
            return;
        }
        if (jfc == null) {
            jfc = new JFileChooser();
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                SRLGUIApp.getApplication().proj = SrlProject.openSrlProject(jfc.getSelectedFile());
                proj = SRLGUIApp.getApplication().proj;
                for (WordListSet wl : proj.wordlists) {
                    proj.corpus.listenToWordListSet(wl);
                    for (Map.Entry<String, ListenableSet<WordListEntry>> l : wl.wordLists.entrySet()) {
                        proj.corpus.listenToWordList(l.getKey(), l.getValue());
                    }
                }
                reloadProject();
            } catch (RuntimeException x) {
                if (x.getMessage().matches("Lock obtain timed out: SimpleFSLock.*")) {
                    JOptionPane.showMessageDialog(this.getFrame(), "Corpus locked! This may occur if SRL Editor failed to shut down properly.\nPlease ensure no other copies of SRL Editor are running and\ndelete the file corpus/write.lock from your project directory.",
                            "Corpus Lock", JOptionPane.ERROR_MESSAGE);
                } else {
                    x.printStackTrace();
                    JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not open project", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not open project", JOptionPane.ERROR_MESSAGE);
            }
        }
        } finally {
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
    }

    @Action
    public void saveProject() {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        if (proj != null && proj.isModified()) {
            try {
                proj.writeProject();
                    SRLGUIApp.getApplication().clearModified();
            } catch (IOException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
            } catch (CorpusConcurrencyException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not save project", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Action
    public void addRuleSet() {
        String[] opts = {"Entity", "Template"};
        int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
        if (ruleType == JOptionPane.CLOSED_OPTION) {
            return;
        }
        addRuleSet(ruleType);
    }

    public void addRuleSet(int ruleType) {
        String name = JOptionPane.showInputDialog(this.getFrame(), "Rule Set Name: ");
        if (name == null) {
            return;
        }
        if(name.matches(".*[<>:\"/\\\\\\|\\?\\*].*") ||
                name.matches(".*\\s.*") ||
                name.equals("")) {
            JOptionPane.showMessageDialog(getFrame(), "Rule set name cannot contain whitespace or the following characters: < > : \" \\ | ? *", 
                    "Invalid rule set name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        for(RuleSet rs : ruleType == Rule.ENTITY_RULE ? proj.entityRulesets : proj.templateRulesets) {
            if(rs.name.equals(name)) {
                JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add rule set", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        RuleSet rs = new RuleSet(ruleType, name);
        if (ruleType == Rule.ENTITY_RULE) {
            proj.entityRulesets.add(rs);
        } else {
            proj.templateRulesets.add(rs);
        }
        SRLGUIApp.getApplication().setModified();
        if (ruleType == Rule.ENTITY_RULE) {
            SRLGUIApp.getApplication().entityRuleSets.put(name, rs);
        } else {
            SRLGUIApp.getApplication().templateRuleSets.put(name, rs);
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
        ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                ruleSet,
                ruleSet.getChildCount());
        mainTree.scrollPathToVisible(new TreePath(node.getPath()));
    }

    public void removeRuleSet(int ruleType, String setName, DefaultMutableTreeNode node) {
        SrlProject proj = SRLGUIApp.getApplication().proj;
        List<RuleSet> ruleSetList = (ruleType == Rule.ENTITY_RULE ? proj.entityRulesets : proj.templateRulesets);
        Iterator<RuleSet> rsIter = ruleSetList.iterator();
        while (rsIter.hasNext()) {
            RuleSet rs = rsIter.next();
            if (rs.name.equals(setName)) {
                rsIter.remove();
                break;
            }
        }
        if (ruleType == Rule.ENTITY_RULE) {
            SRLGUIApp.getApplication().entityRuleSets.remove(setName);
        } else {
            SRLGUIApp.getApplication().templateRuleSets.remove(setName);
        }
        DefaultTreeModel dtm = (DefaultTreeModel) mainTree.getModel();
        dtm.removeNodeFromParent(node);
        SRLGUIApp.getApplication().setModified();
    }

    @Action
    public void addWordList() {
        String name = JOptionPane.showInputDialog(this.getFrame(), "Word List Set Name: ");
        if (name == null) {
            return;
        }
        if(name.matches(".*\\W.*") ||
                name.equals("")) {
            JOptionPane.showMessageDialog(getFrame(), "Word list name cannot contain non-word characters. (Not A-Z or _)", 
                    "Invalid word list name", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SrlProject proj = SRLGUIApp.getApplication().proj;
        for(WordListSet wl : proj.wordlists) {
            if(wl.name.equals(name)) {
                 JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        WordListSet wl = new WordListSet(name, proj.corpus.getProcessor());
        proj.corpus.listenToWordListSet(wl);
        proj.wordlists.add(wl);
        SRLGUIApp.getApplication().setModified();
        SRLGUIApp.getApplication().wordLists.put(name, wl);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                SRLGUIApp.getApplication().wordList,
                SRLGUIApp.getApplication().wordList.getChildCount());
        mainTree.scrollPathToVisible(new TreePath(node.getPath()));
    }

    public void removeWordList(DefaultMutableTreeNode node) {
        String name = node.getUserObject().toString();
        SrlProject proj = SRLGUIApp.getApplication().proj;
        WordListSet wl = SRLGUIApp.getApplication().wordLists.get(name);
        proj.wordlists.remove(wl);
        SRLGUIApp.getApplication().setModified();
        SRLGUIApp.getApplication().wordLists.remove(name);
        ((DefaultTreeModel) mainTree.getModel()).removeNodeFromParent(node);
    }

    private class CustomEncodingFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "Plain text (Non-default encoding)";
        }
        
    }
    
    private class NullTask extends Task {

        public NullTask() {
            super(SRLGUIApp.getApplication());
        }

        @Override
        protected Object doInBackground() throws Exception {
            return null;
        }
        
    }
    
    @Action
    public Task addCorpusDoc() {
        try {
            if(jfc != null)
                jfc = new JFileChooser(jfc.getSelectedFile());
            else
                jfc = new JFileChooser();
            jfc.setMultiSelectionEnabled(true);
            jfc.addChoosableFileFilter(new CustomEncodingFilter());
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                 @Override
                   public boolean accept(File f) {
                         return true;
                  }

            @Override
            public String getDescription() {
                return "Plain text (" + Charset.defaultCharset().name() + ")";
            }
        });
        String encoding = null;
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getFileFilter() instanceof CustomEncodingFilter) {
                encoding = JOptionPane.showInputDialog(this.getFrame(), "Enter encoding (e.g., \"UTF-8\"): ", "");
                if(encoding == null) {
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
                try {
                    Charset.forName(encoding);
                } catch(Exception x) {
                    JOptionPane.showMessageDialog(this.getFrame(), "Invalid encoding", "Cannot load", JOptionPane.WARNING_MESSAGE);
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
            }
        }
        File[] sf = jfc.getSelectedFiles();
        return new DocumentLoadThread(encoding, sf, false);
        } finally {
            jfc.setMultiSelectionEnabled(false);
        jfc.resetChoosableFileFilters();

        }
    }

    private class AddCorpusDocTask extends org.jdesktop.application.Task<Object, Void> {
        AddCorpusDocTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddCorpusDocTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }
    
    private class DocumentLoadThread extends Task {
        String encoding;
        File[] selectedFiles;
        boolean tagged;
        
        public DocumentLoadThread(String encoding, File[] selectedFiles, boolean tagged)
        {
            super(SRLGUIApp.getApplication());
            this.encoding = encoding;
            this.selectedFiles = selectedFiles;
            this.tagged = tagged;
        }

        
        public Object doInBackground() throws Exception {
            try {
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                long lockID = corpus.reopenIndex(true);
                try {
                    int replaceDoc = 0; // 0=? 1=YES -1=NO
                    JPanel p = getPanel(SRLGUIApp.getApplication().SRL_CORPUS, "");
                    int i = 0;
                    for (File file : selectedFiles) {
                        String fName = file.getName().replaceAll("[^A-Za-z0-9]", "");
                        setMessage("Adding " + fName);
                        setProgress((float)i++ / (float)selectedFiles.length);
                        BufferedReader br;
                        if(encoding == null) {
                            br= new BufferedReader(new FileReader(file));
                        } else {
                            br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
                        }
                        StringBuffer contents = new StringBuffer();
                        String in = br.readLine();
                        while (in != null) {
                            contents.append(in + "\n");
                            in = br.readLine();
                        }
                        br.close();
                        if(corpus.containsDoc(fName)) {
                            if(replaceDoc == 0) {
                                String[] opts = { "Skip", "Replace", "Skip All", "Replace All" };
                                int opt = JOptionPane.showOptionDialog(SRLGUIApp.getApplication().getMainFrame(), "Document called "+fName+" already exists", "Duplicate Document", 
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                                if(opt == 2) { replaceDoc = -1; }
                                if(opt == 3) { replaceDoc = 1; }
                                if(opt == 0 || opt == 2) {
                                    corpus.updateDoc(fName, contents.toString());
                                }
                            } else if(replaceDoc == 1) {
                                corpus.updateDoc(fName, contents.toString());
                            }
                        } else
                            corpus.addDoc(fName, contents.toString(), tagged);
                        if (p != null) {
                            ((CorpusDocumentPanel) p).addDoc(fName);
                        }
                    }
                } finally {
                    corpus.closeIndex(lockID);
                }
                corpus.optimizeIndex();
            } catch (Exception x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Could not add documents to corpus", JOptionPane.ERROR_MESSAGE);
            }
            if(selectedFiles.length > 0) {
                setMessage("All documents added");
                setProgress(1.0f);
            }
            return null;
        }
    }

    @Action
    public Task tagCorpus() {
        return new TagCorpusTask();
    }
    
    public void enableSave() {
        jButton3.setEnabled(true);
        jMenuItem1.setEnabled(true);
    }
    
    public void disableSave() {
        jButton3.setEnabled(false);
        jMenuItem1.setEnabled(false);
    }

    private class TagCorpusTask extends Task implements mccrae.tools.process.ProgressMonitor {

        TagCorpusTask() {
            super(SRLGUIApp.getApplication());
        }

        public Object doInBackground() throws Exception {
            try {
                CorpusExtractor ce = new CorpusExtractor(SRLGUIApp.getApplication().proj.corpus);
                LinkedList<CorpusExtractor.Overlap> overlaps = new LinkedList<CorpusExtractor.Overlap>();
                ce.tagCorpus(SRLGUIApp.getApplication().proj.entityRulesets,overlaps, this);
                if(overlaps.isEmpty())
                    JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Corpus tagging complete", "Corpus tagger", JOptionPane.INFORMATION_MESSAGE);
                else {
                    OverlapMessageDialog omd = new OverlapMessageDialog(SRLGUIApp.getApplication().getMainFrame(), true, overlaps);
                    omd.setVisible(true);
                }
            } catch (IOException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Corpus Tagging Failed", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }

        public void setMessageVal(String s) {
            setMessage(s);
        }

        public void setProgressVal(float f) {
            setProgress(f);
        }
        
        
    }

    @Action
    public Task extractTemplates() {
        return new ExtractTemplatesTask();
    }
    
    private class ExtractTemplatesTask extends Task implements mccrae.tools.process.ProgressMonitor {

        public ExtractTemplatesTask() {
            super(SRLGUIApp.getApplication());
        }
        

        public Object doInBackground() throws Exception {
            try {
                CorpusExtractor ce = new CorpusExtractor(SRLGUIApp.getApplication().proj.corpus);
                ce.extractTemplates(SRLGUIApp.getApplication().proj.templateRulesets, this);
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Template Extraction Complete", "Template Extraction", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Corpus Tagging Failed", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }

        public void setMessageVal(String s) {
            setMessage(s);
        }

        public void setProgressVal(float f) {
            setProgress(f);
        }
        
    }

    public static int searchCount = 1;
    
    @Action
    public void searchCorpus() {
        String query = JOptionPane.showInputDialog(getFrame(), "Query", "");
        if(query != null && query.length() != 0) {
            String title = "Search " + searchCount++;
            JPanel c =  new SearchPanel(query, title);
            rightPane.addTab(title, c);
             try {
                rightPane.setTabComponentAt(rightPane.getTabCount() - 1, new CloseTabButton(SRLGUIApp.SRL_SEARCH, title,searchIcon));
            } catch(NoSuchMethodError e) {
                // Java 1.5 compatibility
                rightPane.setIconAt(rightPane.getTabCount() - 1, new CloseTabIcon());
            }
            rightPane.setSelectedComponent(c);
        }
    }

    @Action
    public void openWiki() {
        try {
            Desktop.getDesktop().browse(new URI("http://code.google.com/p/srl-editor/w/list"));
        } catch(Exception x) {
            JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not open external browser", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public Task writeTagged() {
        return new WriteTaggedTask(getApplication());
    }

    private class WriteTaggedTask extends org.jdesktop.application.Task<Object, Void> {
        WriteTaggedTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to WriteTaggedTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            jfc.setFileSelectionMode(jfc.DIRECTORIES_ONLY);
            try {
                if(jfc.showOpenDialog(SRLGUIApp.getApplication().getMainFrame()) !=
                        jfc.APPROVE_OPTION)
                    return null;
                File directory = jfc.getSelectedFile();
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                Set<String> docNames = corpus.getDocNames();
                float i = 0;
                for(String docName : docNames) {
                    setMessage("Writing tagged: " + docName);
                    setProgress((float)i++ / (float)docNames.size());
                    List<String> cont = corpus.getDocTaggedContents(docName);
                    PrintStream out = new PrintStream(new File(directory, docName + ".tagged"));
                    for(String c : cont) {
                        out.println(c);
                    }
                    out.close();
                }
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Tagged documents written", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Could not write document contents",
                        JOptionPane.ERROR_MESSAGE);
            } catch(CorpusConcurrencyException x) {
                
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Could not write document contents",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                jfc.setFileSelectionMode(jfc.FILES_ONLY);
            }
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task writeTemplates() {
        return new WriteTemplatesTask(getApplication());
    }

    private class WriteTemplatesTask extends org.jdesktop.application.Task<Object, Void> {
        WriteTemplatesTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to WriteTemplatesTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            jfc.setFileSelectionMode(jfc.DIRECTORIES_ONLY);
            try {
                if(jfc.showOpenDialog(SRLGUIApp.getApplication().getMainFrame()) !=
                        jfc.APPROVE_OPTION)
                    return null;
                File directory = jfc.getSelectedFile();
                Corpus corpus = SRLGUIApp.getApplication().proj.corpus;
                Set<String> docNames = corpus.getDocNames();
                float i = 0;
                for(String docName : docNames) {
                    setMessage("Writing template: " + docName);
                    setProgress(i++ / (float)docNames.size());
                    List<String> cont = corpus.getDocTemplateExtractions(docName);
                    PrintStream out = new PrintStream(new File(directory, docName + ".templates"));
                    for(String c : cont) {
                        out.println(c);
                    }
                    out.close();
                }
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), "Templates documents written", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch(IOException x) {
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Could not write document contents",
                        JOptionPane.ERROR_MESSAGE);
            } catch(CorpusConcurrencyException x) {
                
                x.printStackTrace();
                JOptionPane.showMessageDialog(SRLGUIApp.getApplication().getMainFrame(), x.getMessage(), "Could not write document contents",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                jfc.setFileSelectionMode(jfc.FILES_ONLY);
            }
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public void importRuleSet() {
        try {
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().matches(".*\\.rule\\.srl") || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "SRL rule files (*.rule.srl)";
                }
            });
            if(jfc.showOpenDialog(getFrame()) != jfc.APPROVE_OPTION)
                return;
            String[] opts = {"Entity", "Template"};
            int ruleType = JOptionPane.showOptionDialog(this.getFrame(), "Open as entity or template rule set?", "Open rule set", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opts, "Entity");
            File f = jfc.getSelectedFile();
            String name;
            if(f.getName().matches(".*\\.rule\\.srl"))
                name = f.getName().substring(0, f.getName().length()-9);
            else
                name = f.getName();
            SrlProject proj = SRLGUIApp.getApplication().proj;
            RuleSet rs = RuleSet.loadFromFile(f, ruleType);
            if (ruleType == Rule.ENTITY_RULE) {
                proj.entityRulesets.add(rs);
            } else {
                proj.templateRulesets.add(rs);
            }
            SRLGUIApp.getApplication().setModified();
            if (ruleType == Rule.ENTITY_RULE) {
                SRLGUIApp.getApplication().entityRuleSets.put(name, rs);
            } else {
                SRLGUIApp.getApplication().templateRuleSets.put(name, rs);
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
            DefaultMutableTreeNode ruleSet = (ruleType == Rule.ENTITY_RULE ? SRLGUIApp.getApplication().entityRules : SRLGUIApp.getApplication().templateRules);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                ruleSet,
                ruleSet.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));
        } catch(Exception x) { 
            x.printStackTrace();
            JOptionPane.showMessageDialog(this.getFrame(), x.getMessage(), "Could not import ruleset", JOptionPane.ERROR_MESSAGE);
        } finally {
            jfc.resetChoosableFileFilters();
        }
    }

    @Action
    public void importWordList() {
        try {
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().matches(".*\\.wordlist\\.srl") || f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "SRL word list files (*.wordlist.srl)";
                }
            });
            if(jfc.showOpenDialog(getFrame()) != jfc.APPROVE_OPTION)
                return;
            File f = jfc.getSelectedFile();
            String name;
            if(f.getName().matches(".*\\.wordlist\\.srl"))
                name = f.getName().substring(0, f.getName().length()-13);
            else
                name = f.getName();
            if(name.matches(".*\\W.*") || name.length()==0) {
                JOptionPane.showMessageDialog(getFrame(), name + " is not a valid name... name must be only word characters", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SrlProject proj = SRLGUIApp.getApplication().proj;
            for(WordListSet wl : proj.wordlists) {
               if(wl.name.equals(name)) {
                     JOptionPane.showMessageDialog(this.getFrame(), name + " already exists", "Cannot add word list set", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            WordListSet wl = WordListSet.loadFromFile(f, proj.processor);
            proj.corpus.listenToWordListSet(wl);
            proj.wordlists.add(wl);
            SRLGUIApp.getApplication().setModified();
            SRLGUIApp.getApplication().wordLists.put(name, wl);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
            ((DefaultTreeModel) mainTree.getModel()).insertNodeInto(node,
                    SRLGUIApp.getApplication().wordList,
                    SRLGUIApp.getApplication().wordList.getChildCount());
            mainTree.scrollPathToVisible(new TreePath(node.getPath()));
        } catch(Exception x) {
            x.printStackTrace();
            JOptionPane.showMessageDialog(getFrame(), x.getMessage(), "Could not import word list", JOptionPane.ERROR_MESSAGE);
        } finally {
            jfc.resetChoosableFileFilters();
        }
    }

    @Action
    public Task importTagged() {
         try {
            // JOptionPane.showMessageDialog(getFrame(), "This needs fixing... please email jmccrae@nii.ac.jp if this has somehow made it to a release version");
            if (jfc == null) {
               jfc = new JFileChooser();
            }
            jfc.setMultiSelectionEnabled(true);
            jfc.addChoosableFileFilter(new CustomEncodingFilter());
            jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

                 @Override
                   public boolean accept(File f) {
                         return true;
                  }

            @Override
            public String getDescription() {
                return "Plain text (" + Charset.defaultCharset().name() + ")";
            }
        });
        String encoding = null;
        if (jfc.showOpenDialog(this.getFrame()) == JFileChooser.APPROVE_OPTION) {
            if(jfc.getFileFilter() instanceof CustomEncodingFilter) {
                encoding = JOptionPane.showInputDialog(this.getFrame(), "Enter encoding (e.g., \"UTF-8\"): ", "");
                if(encoding == null) {
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
                try {
                    Charset.forName(encoding);
                } catch(Exception x) {
                    JOptionPane.showMessageDialog(this.getFrame(), "Invalid encoding", "Cannot load", JOptionPane.WARNING_MESSAGE);
                    jfc.setMultiSelectionEnabled(false);
                    jfc.resetChoosableFileFilters();
                    return new NullTask();
                }
            }
        }
        File[] sf = jfc.getSelectedFiles();
        return new DocumentLoadThread(encoding, sf, true);
        } finally {
            jfc.setMultiSelectionEnabled(false);
        jfc.resetChoosableFileFilters();

        }
    }

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTree mainTree;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane rightPane;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private final Icon ruleSetIcon;
    private final Icon wordListIcon;
    private final Icon corpusIcon;
    private final Icon closeTabIcon;
    private final Icon searchIcon;
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
