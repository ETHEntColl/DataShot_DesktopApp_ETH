/**
 * SpecimenDetailsViewPane.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2009 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;

import edu.harvard.mcz.imagecapture.data.AgentNameComboBoxModel;
import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorLifeCycle;
import edu.harvard.mcz.imagecapture.data.CollectorTableModel;
import edu.harvard.mcz.imagecapture.data.Determination;
import edu.harvard.mcz.imagecapture.data.DeterminationTableModel;
import edu.harvard.mcz.imagecapture.data.Features;
import edu.harvard.mcz.imagecapture.data.HibernateUtil;
import edu.harvard.mcz.imagecapture.data.HigherGeographyComboBoxModel;
import edu.harvard.mcz.imagecapture.data.HigherTaxonLifeCycle;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.LatLong;
import edu.harvard.mcz.imagecapture.data.LifeStage;
import edu.harvard.mcz.imagecapture.data.LocationInCollection;
import edu.harvard.mcz.imagecapture.data.MCZbaseAuthAgentName;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRec;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRecLifeCycle;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.NatureOfId;
import edu.harvard.mcz.imagecapture.data.NumberLifeCycle;
import edu.harvard.mcz.imagecapture.data.NumberTableModel;
import edu.harvard.mcz.imagecapture.data.Sex;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPart;
import edu.harvard.mcz.imagecapture.data.SpecimenPartLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPartsAttrTableModel;
import edu.harvard.mcz.imagecapture.data.SpecimenPartsTableModel;
import edu.harvard.mcz.imagecapture.data.Tracking;
import edu.harvard.mcz.imagecapture.data.TrackingLifeCycle;
import edu.harvard.mcz.imagecapture.data.TypeStatus;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.ui.ButtonEditor;
import edu.harvard.mcz.imagecapture.ui.ButtonRenderer;
import edu.harvard.mcz.imagecapture.ui.FilteringAgentJComboBox;
import edu.harvard.mcz.imagecapture.ui.FilteringGeogJComboBox;
import edu.harvard.mcz.imagecapture.ui.PicklistTableCellEditor;
import edu.harvard.mcz.imagecapture.ui.ValidatingTableCellEditor;

import java.awt.Dimension;

import javax.swing.JTextField;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JComboBox;

import java.awt.Insets;
import java.net.URL;

import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.hibernate.TransactionException;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 * JPanel for editing a record of a Specimen in a details view for that specimen. 
 * 
 * @author Paul J. Morris
 * 
 *  TODO: BugID: 10 add length limits (remaining to do for Number/Collector tables, 
 *  and for JComboBox fields).
 */
public class SpecimenDetailsViewPane extends JPanel {
	
	private static final Log log = LogFactory.getLog(SpecimenDetailsViewPane.class);
	
	private static final long serialVersionUID = 3716072190995030749L;
	
	private static final int STATE_CLEAN = 0;
	private static final int STATE_DIRTY = 1;
	
	
	private Specimen specimen;  //  @jve:decl-index=0:
	private Specimen lastEditedSpecimen = null;
	
	private SpecimenControler myControler = null;
	private int state;   // dirty if data in controls has been changed and not saved to specimen.
	
	private JTextField jTextFieldStatus = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldBarcode = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldGenus = null;
	private JTextField jTextFieldSpecies = null;
	private JTextField jTextFieldSubspecies = null;
	private JTextField jTextFieldLocality = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JButton jButton = null;
	private JLabel jLabel5 = null;
	private JComboBox<String> jComboBoxCollection = null;  
	private JLabel jLabel6 = null;
	private JTextField jTextFieldLastUpdatedBy = null;
	private JScrollPane jScrollPaneCollectors = null;
	private JTable jTableCollectors = null;
	private JScrollPane jScrollPaneSpecimenParts = null;
	private JTable jTableSpecimenParts = null;
	private JScrollPane jScrollPaneNumbers = null;
	private JTable jTableNumbers = null;
	
	private int clickedOnPartsRow;
	private JPopupMenu jPopupSpecimenParts;
	private int clickedOnCollsRow;
	private JPopupMenu jPopupCollectors;
	private int clickedOnNumsRow;
	private JPopupMenu jPopupNumbers;	
	
	private JLabel jLabel7 = null;
	private JLabel jLabel8 = null;
	private JTextField jTextFieldDateLastUpdated = null;
	private JTextField jTextFieldCreator = null;
	private JLabel jLabel9 = null;
	private JTextField jTextFieldDateCreated = null;
	private JLabel jLabel10 = null;
	private JLabel jLabel11 = null;
	private JButton jButtonNumbersAdd = null;
	private JButton jButtonCollsAdd = null;
	private JScrollPane jScrollPane = null;
	private JTextPane jTextPaneWarnings = null;
	private JLabel jLabel12 = null;
	private JTextField jTextFieldDrawerNumber = null;
	private JLabel jLabel13 = null;
	private JLabel jLabel14 = null;
	private JLabel jLabel15 = null;
	private JTextField jTextFieldVerbatimLocality = null;
	//private JLabel lblHigherGeography;
	//private FilteringGeogJComboBox comboBoxHigherGeog;
	private JButton jButtonGeoreference = null;
	//private JTextField jTextFieldCountry = null;
	private JComboBox<String> jComboBoxCountry = null;
	//allie change
	//private JTextField jTextFieldPrimaryDivision = null;
	private JComboBox jComboBoxPrimaryDivision = null;
	private JLabel jLabel16 = null;
	private JLabel jLabel17 = null;
	private JLabel jLabelTribe;
	private JComboBox<String> jComboBoxFamily = null;
	private JComboBox<String> jComboBoxSubfamily = null;
	private JTextField jTextFieldTribe = null;
	private JLabel jLabel19 = null;
	private JLabel jLabel20 = null;
	private JLabel jLabel21 = null;
	private JComboBox<String> jComboBoxSex = null;
	private JComboBox<String> jComboBoxFeatures = null;
	private JComboBox<String> jComboBoxLifeStage = null;
	private JLabel jLabel22 = null;
	private JTextField jTextFieldDateNos = null;
	private JTextField jTextFieldDateEmerged = null;
	private JTextField jTextFieldDateEmergedIndicator = null;
	private JTextField jTextFieldDateCollected = null;
	private JTextField jTextFieldDateCollectedIndicator = null;
	private JLabel jLabel25 = null;
	private JLabel jLabel26 = null;
	private JLabel jLabel27 = null;
	private JTextField jTextFieldInfraspecificEpithet = null;
	private JLabel jLabel28 = null;
	private JTextField jTextFieldInfraspecificRank = null;
	private JLabel jLabel29 = null;
	private JTextField jTextFieldAuthorship = null;
	private JLabel jLabel30 = null;
	private JTextField jTextFieldUnnamedForm = null;
	private JLabel jLabel32 = null;
	private JLabel jLabelElevation;
	private JTextField jTextFieldMinElevation = null;
	private JTextField textFieldMaxElev = null;
	private JComboBox comboBoxElevUnits = null;
	private JTextField jTextFieldCollectingMethod = null;
	private JLabel jLabel33 = null;
	private JLabel jLabel34 = null;
	private JLabel jLabel35 = null;
	private JTextArea jTextAreaSpecimenNotes = null;
	private JCheckBox jCheckBoxValidDistributionFlag = null;
	private JLabel jLabel36 = null;
	private JLabel jLabel37 = null;
	private JLabel jLabel38 = null;
	private JLabel jLabel39 = null;
	private JLabel jLabel40 = null;
	private JTextField jLabelMigrationStatus = null;
	private JTextField jTextFieldQuestions = null;
	// private JTextField jTextFieldPreparationType = null;
	private JButton jButtonAddPreparationType;
	private JTextField jTextFieldAssociatedTaxon = null;
	private JTextField jTextFieldHabitat = null;
	private JLabel jLabel41 = null;
	private JComboBox<String> jComboBoxWorkflowStatus = null;
	private JLabel jLabel42 = null;
	private JComboBox<String> jComboBoxLocationInCollection = null;
	private JLabel jLabel43 = null;
	private JTextField jTextFieldInferences = null;
	private JButton jButtonGetHistory = null;
	private JButton jButtonCopyPrev = null;
	private JButton jButtonCopyPrevTaxon = null;
	private JButton jButtonNext = null;
	private SpecimenDetailsViewPane thisPane = null;
    private JButton jButtonPrevious = null;
    private JPanel jPanel1 = null;   // panel for navigation buttons
	private JTextField jTextFieldISODate = null;
	private JButton jButtonDeterminations = null;
	private JLabel jLabel31 = null;
	private JTextField jTextFieldCitedInPub = null;
	private JScrollPane jScrollPaneNotes = null;
	//private JTextField jScrollPaneNotes = null;
	private JLabel jLabel44 = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JButton jButtonSpecificLocality = null;
	private JLabel jTextFieldImageCount = null;
	private JLabel lblTo;
	private JLabel lblMicrohabitat;
	private JTextField microhabitTextField;
	private JLabel lblNatureofid;
	private JComboBox<String> jComboBoxNatureOfId;
	private JLabel lblIdDate;
	private JTextField jTextFieldDateDetermined;
	
	//allie change
	//private JTextField jCBDeterminer;
	//private FilteringAgentJComboBox jCBDeterminer;
	private JComboBox<String> jCBDeterminer = null;
	
	private JLabel lblIdRemarks;
	private JTextField jTextFieldIdRemarks;
	private JLabel lblTypestatus;
	private JComboBox<String> cbTypeStatus;

	//allie add
	Specimen previousSpecimen = null;
	
	/**
	 * Construct an instance of a SpecimenDetailsViewPane showing the data present
	 * in aSpecimenInstance. 
	 * 
	 * @param aSpecimenInstance the Specimen instance to display for editing.
	 */
	public SpecimenDetailsViewPane(Specimen aSpecimenInstance, SpecimenControler aControler) { 
		specimen = aSpecimenInstance;
		SpecimenLifeCycle s = new SpecimenLifeCycle();
		setStateToClean();
//		SpecimenPartAttributeLifeCycle spals = new SpecimenPartAttributeLifeCycle();
//		Iterator<SpecimenPart> i = specimen.getSpecimenParts().iterator();
//		while (i.hasNext()) { 
//			Iterator<SpecimenPartAttribute> ia = i.next().getAttributeCollection().iterator();
//			while (ia.hasNext()) { 
//				try {
//					SpecimenPartAttribute spa = ia.next();
//					log.debug(spa.getSpecimenPartAttributeId());
//					spals.attachDirty(spa);
//					log.debug(spa.getSpecimenPartAttributeId());
//				} catch (SaveFailedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		try {
			s.attachClean(specimen);
			myControler = aControler;
			initialize();
			setValues();	
			thisPane = this;
		} catch (SessionException e) { 
			log.debug(e.getMessage(),e);
			Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Database Connection Error.");
			HibernateUtil.terminateSessionFactory();
			this.setVisible(false);			
		} catch (TransactionException e) { 
			log.debug(e.getMessage(),e);
			Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Database Connection Error.");
			HibernateUtil.terminateSessionFactory();
			this.setVisible(false);
		}
	}

	
    /** initializes the specimen details view pane.
	 *  Note, contains comments indicating how to enable visual designer with this class. 
	 */
	private void initialize() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(0);
		borderLayout.setVgap(0);
		this.setLayout(borderLayout);
		this.setSize(new Dimension(594, 1000));
		//this.setPreferredSize(new Dimension(490, 917));
	    this.add(getJTextFieldStatus(), BorderLayout.SOUTH);
	    
	    // Un-comment this line to use design tool.
	    //    this.add(getJPanel(), BorderLayout.CENTER);
	        
	    // Comment this block out to use design tool.
	    //   see also getCbTypeStatus
	        
	    if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
	    		 ImageCaptureProperties.KEY_DETAILS_SCROLL).equals(ImageCaptureProperties.VALUE_DETAILS_SCROLL_FORCE_ON)) { 
	    JScrollPane scrollPane = new JScrollPane(getJPanel(),
	    		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	    		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.add(scrollPane, BorderLayout.CENTER);
	    } else { 
	    	this.add(getJPanel(), BorderLayout.CENTER);
	    }
	    //**	    
	      
	}	
	
	public void setWarning(String warning) { 
		jTextPaneWarnings.setText(warning);
		jTextPaneWarnings.setForeground(Color.RED);
	}
	
	private void setWarnings() { 
		log.debug("In set warnings");
		if (specimen.getICImages()!=null) { 
			log.debug("specimen.getICImages is not null");
			java.util.Iterator<ICImage> i = specimen.getICImages().iterator();
			log.debug(i.hasNext());
			while (i.hasNext()) { 
				log.debug("Checking image " + i );
				ICImage im = i.next();
				String rbc = "";
				if (im.getRawBarcode()!=null) { rbc = im.getRawBarcode(); }
				String ebc = "";
				if (im.getRawExifBarcode()!=null) { ebc = im.getRawExifBarcode(); } 
				if (!rbc.equals(ebc)) { 
					// warn of mismatch, but only if configured to expect both to be present.
					if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
						jTextPaneWarnings.setText("Warning: An image has mismatch between Comment and Barcode.");
						jTextPaneWarnings.setForeground(Color.RED);
						log.debug("Setting: Warning: Image has mismatch between Comment and Barcode.");
					}
				}
			}
		}
	}
		
	private void save() { 
		try { 
		jTextFieldStatus.setText("Saving");
		if (jTableCollectors.isEditing()) { 
		    jTableCollectors.getCellEditor().stopCellEditing();
		}
		if (jTableSpecimenParts.isEditing()) { 
		    jTableSpecimenParts.getCellEditor().stopCellEditing();
		}		
		if (jTableNumbers.isEditing()) { 
			log.debug("jTableNumbers IS editing!!!");
		    jTableNumbers.getCellEditor().stopCellEditing();
		}
		
	    if (cbTypeStatus.getSelectedIndex()==-1 && cbTypeStatus.getSelectedItem()==null) { 
			specimen.setTypeStatus(Specimen.STATUS_NOT_A_TYPE);
	    } else { 
	    	specimen.setTypeStatus((String)cbTypeStatus.getSelectedItem());
	    }
		specimen.setMicrohabitat(microhabitTextField.getText());
		
		if(jComboBoxLocationInCollection.getSelectedItem() != null){
			specimen.setLocationInCollection(jComboBoxLocationInCollection.getSelectedItem().toString());
		}
		
		
		specimen.setDrawerNumber(jTextFieldDrawerNumber.getText());
	    if (jComboBoxFamily.getSelectedIndex()==-1 && jComboBoxFamily.getSelectedItem()==null) { 
	    	specimen.setFamily("");
	    } else {
	    	specimen.setFamily(jComboBoxFamily.getSelectedItem().toString());
	    }
	    if (jComboBoxSubfamily.getSelectedIndex()==-1 && jComboBoxSubfamily.getSelectedItem()==null) { 
	    	specimen.setSubfamily("");
	    } else {
	    	specimen.setSubfamily(jComboBoxSubfamily.getSelectedItem().toString());
	    }
		specimen.setTribe(jTextFieldTribe.getText());
	    specimen.setGenus(jTextFieldGenus.getText());
	    specimen.setSpecificEpithet(jTextFieldSpecies.getText());
	    specimen.setSubspecificEpithet(jTextFieldSubspecies.getText());
	    specimen.setInfraspecificEpithet(jTextFieldInfraspecificEpithet.getText());
	    specimen.setInfraspecificRank(jTextFieldInfraspecificRank.getText());
	    specimen.setAuthorship(jTextFieldAuthorship.getText());
	    //TODO: handle the collectors set!

	    //this returns TRUE for the copied item!!
	    log.debug("in save(). specimen numbers size: " + specimen.getNumbers().size());
		log.debug("okok in save(), specimenid is " + specimen.getSpecimenId());

	    if(previousSpecimen != null && previousSpecimen.getNumbers() != null){
	    	log.debug("in save(). prev specimen numbers size: " + previousSpecimen.getNumbers().size());
	    	//specimen.setNumbers(previousSpecimen.getNumbers()); - this gives hibernate exceptions here too!
	    	log.debug("okok in save(), prev specimenid is " + previousSpecimen.getSpecimenId());
	    }

	    //allie change
	    //log.debug("THE jCBDeterminer TEXT IS " + jCBDeterminer.getText());
	    //specimen.setIdentifiedBy(jCBDeterminer.getText());
	    specimen.setIdentifiedBy((String)jCBDeterminer.getSelectedItem());
	    
	    specimen.setDateIdentified(jTextFieldDateDetermined.getText());
	    specimen.setIdentificationRemarks(jTextFieldIdRemarks.getText());
	    if (jComboBoxNatureOfId.getSelectedIndex()==-1 && jComboBoxNatureOfId.getSelectedItem()==null) { 
	    	//specimen.setNatureOfId(NatureOfId.LEGACY);
	    	specimen.setNatureOfId(NatureOfId.EXPERT_ID);
	    } else { 
	    	specimen.setNatureOfId((String)jComboBoxNatureOfId.getSelectedItem());
	    }
	    
	    specimen.setUnNamedForm(jTextFieldUnnamedForm.getText());
	    specimen.setVerbatimLocality(jTextFieldVerbatimLocality.getText());
	    //allieremove
	    /*if (comboBoxHigherGeog.getSelectedIndex()==-1 && comboBoxHigherGeog.getSelectedItem()==null) { 
	    	specimen.setHigherGeography("");
	    } else {
	    	// combo box contains a geography object, obtain the higher geography string.
	    	specimen.setHigherGeography(((HigherGeographyComboBoxModel)comboBoxHigherGeog.getModel()).getSelectedItemHigherGeography());
	    }*/
	    //specimen.setCountry(jTextFieldCountry.getText());
	    specimen.setCountry((String)jComboBoxCountry.getSelectedItem());
	    specimen.setValidDistributionFlag(jCheckBoxValidDistributionFlag.isSelected());
	    specimen.setPrimaryDivison((String)jComboBoxPrimaryDivision.getSelectedItem());
	    specimen.setSpecificLocality(jTextFieldLocality.getText());
	    
	    // Elevations
	    Long min_elev;
	    if (jTextFieldMinElevation.getText().trim().length()==0)  {
	    	min_elev = null;
	    } else { 
	        try { 
	            min_elev = Long.parseLong(jTextFieldMinElevation.getText());
	        } catch (NumberFormatException e) { 
    	    	min_elev = null;
	        }
	    }
	    specimen.setMinimum_elevation(min_elev);
	    Long max_elev;
	    if (textFieldMaxElev.getText().trim().length()==0)  {
	    	max_elev = null;
	    } else { 
	        try { 
	            max_elev = Long.parseLong(textFieldMaxElev.getText());
	        } catch (NumberFormatException e) { 
    	    	max_elev = null;
	        }
	    }	    
	    specimen.setMaximum_elevation(max_elev);
	    if (this.comboBoxElevUnits.getSelectedIndex()==-1 && comboBoxElevUnits.getSelectedItem()==null) { 
	    	specimen.setElev_units("");
	    } else { 
	    	specimen.setElev_units(comboBoxElevUnits.getSelectedItem().toString());
	    }
	    
	    specimen.setCollectingMethod(jTextFieldCollectingMethod.getText());
	    specimen.setIsoDate(jTextFieldISODate.getText());
	    specimen.setDateNos(jTextFieldDateNos.getText());
	    specimen.setDateCollected(jTextFieldDateCollected.getText());
	    specimen.setDateEmerged(jTextFieldDateEmerged.getText());
	    specimen.setDateCollectedIndicator(jTextFieldDateCollectedIndicator.getText());
	    specimen.setDateEmergedIndicator(jTextFieldDateEmergedIndicator.getText());
	    if (jComboBoxCollection.getSelectedIndex()==-1 && jComboBoxCollection.getSelectedItem()==null) { 
	    	specimen.setCollection("");
	    } else {
	    	specimen.setCollection(jComboBoxCollection.getSelectedItem().toString());
	    }
	    if (jComboBoxFeatures.getSelectedIndex()==-1 && jComboBoxFeatures.getSelectedItem()==null) { 
	    	    specimen.setFeatures("");
	    } else {
	        specimen.setFeatures(jComboBoxFeatures.getSelectedItem().toString());
	    }
	    if (jComboBoxLifeStage.getSelectedIndex()==-1 && jComboBoxLifeStage.getSelectedItem()==null) { 
	    	specimen.setLifeStage("");
	    } else {
	    	specimen.setLifeStage(jComboBoxLifeStage.getSelectedItem().toString());
	    }
	    if (jComboBoxSex.getSelectedIndex()==-1 && jComboBoxSex.getSelectedItem()==null) { 
	    	specimen.setSex("");
	    } else {
	        specimen.setSex(jComboBoxSex.getSelectedItem().toString());
	        log.debug("jComboBoxSex selectedIndex=" + jComboBoxSex.getSelectedIndex());
	    }
	    
        log.debug("sex=" + specimen.getSex()); 
        
        specimen.setCitedInPublication(jTextFieldCitedInPub.getText());
	    //specimen.setPreparationType(jTextFieldPreparationType.getText());
	    specimen.setAssociatedTaxon(jTextFieldAssociatedTaxon.getText());
	    specimen.setHabitat(jTextFieldHabitat.getText());
	    specimen.setSpecimenNotes(jTextAreaSpecimenNotes.getText());
	    specimen.setInferences(jTextFieldInferences.getText());
	    specimen.setLastUpdatedBy(Singleton.getSingletonInstance().getUserFullName());
	    specimen.setDateLastUpdated(new Date());
	    specimen.setWorkFlowStatus(jComboBoxWorkflowStatus.getSelectedItem().toString());

	    
	    specimen.setQuestions(jTextFieldQuestions.getText());
	    try { 
	    	myControler.save();   // save the record
            setStateToClean();    // enable the navigation buttons
	    	jTextFieldStatus.setText("Saved");  // inform the user
	    	jTextFieldStatus.setForeground(Color.BLACK);
	    	setWarnings();
	    	jTextFieldLastUpdatedBy.setText(specimen.getLastUpdatedBy());
	    	jTextFieldDateLastUpdated.setText(specimen.getDateLastUpdated().toString());
	    
            //allie added this
            ImageCaptureApp.lastEditedSpecimenCache = specimen;
	    	
	    } catch (SaveFailedException e) { 
	    	setStateToDirty();    // disable the navigation buttons
	    	jTextFieldStatus.setText("Error. " + e.getMessage()); 
	    	jTextFieldStatus.setForeground(MainFrame.BG_COLOR_ERROR);
	    }
	    SpecimenLifeCycle sls = new SpecimenLifeCycle();
		Singleton.getSingletonInstance().getMainFrame().setCount(sls.findSpecimenCount());
		} catch (Exception e) {
			// trap any exception and notify the user
	    	setStateToDirty();    // disable the navigation buttons
	    	jTextFieldStatus.setText("Error. " + e.getMessage()); 
	    	jTextFieldStatus.setForeground(MainFrame.BG_COLOR_ERROR);
		}
		updateDeterminationCount();
	}
	
	private void copyPreviousRecord(){
		log.debug("calling copyPreviousRecord()");
		thisPane.setStateToDirty();
		jTextFieldDateDetermined.setText(previousSpecimen.getDateIdentified());	
		jCBDeterminer.setSelectedItem(previousSpecimen.getIdentifiedBy());
		jTextFieldVerbatimLocality.setText(previousSpecimen.getVerbatimLocality());
		jComboBoxCountry.setSelectedItem(previousSpecimen.getCountry());
		jComboBoxPrimaryDivision.setSelectedItem(previousSpecimen.getPrimaryDivison());
	    // Elevations 
		try { 
		    jTextFieldMinElevation.setText(Long.toString(previousSpecimen.getMinimum_elevation()));
		} catch (Exception e) { 
		    jTextFieldMinElevation.setText("");
		}
		try { 
		    textFieldMaxElev.setText(Long.toString(previousSpecimen.getMaximum_elevation()));
		} catch (Exception e) { 
			textFieldMaxElev.setText("");
		}
		if (previousSpecimen.getElev_units()!=null) { 
		    comboBoxElevUnits.setSelectedItem(previousSpecimen.getElev_units());
		} else {
			comboBoxElevUnits.setSelectedItem("");
		}	
		jTextFieldLocality.setText(previousSpecimen.getSpecificLocality());
		jComboBoxCollection.setSelectedItem(previousSpecimen.getCollection());
		jTableCollectors.setModel(new CollectorTableModel(previousSpecimen.getCollectors()));
		jTextFieldDateNos.setText(previousSpecimen.getDateNos());
		jTextFieldISODate.setText(previousSpecimen.getIsoDate());
		jTextFieldDateEmerged.setText(previousSpecimen.getDateEmerged());
		jTextFieldDateCollectedIndicator.setText(previousSpecimen.getDateCollectedIndicator());
		jTextFieldDateEmergedIndicator.setText(previousSpecimen.getDateEmergedIndicator());
		jTextFieldDateCollected.setText(previousSpecimen.getDateCollected());
		jComboBoxLifeStage.setSelectedItem(previousSpecimen.getLifeStage());
		jComboBoxSex.setSelectedItem(previousSpecimen.getSex());
		jTextFieldAssociatedTaxon.setText(previousSpecimen.getAssociatedTaxon());
		jTextFieldHabitat.setText(previousSpecimen.getHabitat());
		microhabitTextField.setText(previousSpecimen.getMicrohabitat());
		jTextAreaSpecimenNotes.setText(previousSpecimen.getSpecimenNotes());
		jTextFieldInferences.setText(previousSpecimen.getInferences());
		
		//+numbers		
		//this works and is how you need to copy over the table values!!!!!!
		specimen.getNumbers().clear();
		Iterator<edu.harvard.mcz.imagecapture.data.Number> iter = previousSpecimen.getNumbers().iterator();
		while(iter.hasNext()){
			specimen.getNumbers().add((edu.harvard.mcz.imagecapture.data.Number)iter.next());
		}
		jTableNumbers.setModel(new NumberTableModel(specimen.getNumbers()));
		
		// Setting the model will overwrite the existing cell editor bound 
		// to the column model, so we need to add it again.
		JTextField field1 = new JTextField();
		field1.setInputVerifier(MetadataRetriever.getInputVerifier(edu.harvard.mcz.imagecapture.data.Number.class, "Number", field1));
		field1.setVerifyInputWhenFocusTarget(true);
		jTableNumbers.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field1));
		JComboBox jComboNumberTypes = new JComboBox();
		jComboNumberTypes.setModel(new DefaultComboBoxModel(NumberLifeCycle.getDistinctTypes()));
		jComboNumberTypes.setEditable(true);
		TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
		typeColumn.setCellEditor(new DefaultCellEditor(jComboNumberTypes));

		
		//+ verify the georeference data (we do want it all copied)
		//+ preparation type (the whole table!) = specimen parts
		jTableSpecimenParts.setModel(new SpecimenPartsTableModel(previousSpecimen.getSpecimenParts()));
		jTableSpecimenParts.getColumnModel().getColumn(0).setPreferredWidth(90);
		for (int i = 0; i < jTableSpecimenParts.getColumnCount(); i++) {
		    TableColumn column = jTableSpecimenParts.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setPreferredWidth(120); 
		    } else {
		        column.setPreferredWidth(50);
		    }
		}
		
		specimen.getSpecimenParts().clear();
		Iterator<edu.harvard.mcz.imagecapture.data.SpecimenPart> iterp = previousSpecimen.getSpecimenParts().iterator();
		while(iterp.hasNext()){
			specimen.getSpecimenParts().add((edu.harvard.mcz.imagecapture.data.SpecimenPart)iterp.next());
		}
		jTableSpecimenParts.setModel(new SpecimenPartsTableModel(specimen.getSpecimenParts()));
		
		
		//collectors - copy
		specimen.getCollectors().clear();
		Iterator<edu.harvard.mcz.imagecapture.data.Collector> iterc = previousSpecimen.getCollectors().iterator();
		while(iterc.hasNext()){
			specimen.getCollectors().add((edu.harvard.mcz.imagecapture.data.Collector)iterc.next());
		}		
		jTableCollectors.setModel(new CollectorTableModel(specimen.getCollectors()));

		
		//+collectors
		CollectorLifeCycle cls = new CollectorLifeCycle();
		JComboBox jComboBoxCollector = new JComboBox(cls.getDistinctCollectors());
		jComboBoxCollector.setEditable(true);
		//field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(jComboBoxCollector));
		AutoCompleteDecorator.decorate(jComboBoxCollector);		
		
		//+determinations 
		specimen.getDeterminations().clear();
		Iterator<edu.harvard.mcz.imagecapture.data.Determination> iterd = previousSpecimen.getDeterminations().iterator();
		while(iterd.hasNext()){
			Determination prevdet = (edu.harvard.mcz.imagecapture.data.Determination)iterd.next();
			Determination newdet = new Determination();
			newdet.setGenus(prevdet.getGenus());
			newdet.setSpecificEpithet(prevdet.getSpecificEpithet());
			newdet.setSubspecificEpithet(prevdet.getSubspecificEpithet());
			newdet.setInfraspecificEpithet(prevdet.getInfraspecificEpithet());
			newdet.setInfraspecificRank(prevdet.getInfraspecificRank());
			newdet.setAuthorship(prevdet.getAuthorship());
			newdet.setUnNamedForm(prevdet.getUnNamedForm());
			newdet.setIdentifiedBy(prevdet.getIdentifiedBy());
			newdet.setTypeStatus(prevdet.getTypeStatus());
			newdet.setSpeciesNumber(prevdet.getSpeciesNumber());
			newdet.setVerbatimText(prevdet.getVerbatimText());
			newdet.setNatureOfId(prevdet.getNatureOfId());
			newdet.setDateIdentified(prevdet.getDateIdentified());
			newdet.setRemarks(prevdet.getRemarks());				
			newdet.setSpecimen(specimen);
			specimen.getDeterminations().add(newdet);
			
		}		
				
	
		//+georeference
		Set<LatLong> georeferences = specimen.getLatLong();
		LatLong newgeo = georeferences.iterator().next();
		newgeo.setSpecimenId(specimen);
		
		Iterator<edu.harvard.mcz.imagecapture.data.LatLong> iterl = previousSpecimen.getLatLong().iterator();
		while(iterl.hasNext()){
			LatLong prevgeo = (edu.harvard.mcz.imagecapture.data.LatLong)iterl.next();
			newgeo.setAcceptedLatLongFg(prevgeo.getAcceptedLatLongFg());
			newgeo.setAcceptedLatLongFg(prevgeo.getAcceptedLatLongFg());
			newgeo.setDatum(prevgeo.getDatum());
			newgeo.setDecLat(prevgeo.getDecLat());
			newgeo.setDecLatMin(prevgeo.getDecLatMin());
			newgeo.setDecLong(prevgeo.getDecLong());
			newgeo.setDecLongMin(prevgeo.getDecLongMin());
			newgeo.setDeterminedByAgent(prevgeo.getDeterminedByAgent());
			newgeo.setDeterminedDate(prevgeo.getDeterminedDate());
			newgeo.setExtent(prevgeo.getExtent());
			newgeo.setFieldVerifiedFg(prevgeo.getFieldVerifiedFg());
			newgeo.setGeorefmethod(prevgeo.getGeorefmethod());
			newgeo.setGpsaccuracy(prevgeo.getGpsaccuracy());
			newgeo.setLatDeg(prevgeo.getLatDeg());
			newgeo.setLatDir(prevgeo.getLatDir());
			newgeo.setLatLongForNnpFg(prevgeo.getLatLongForNnpFg());
			newgeo.setLatLongRefSource(prevgeo.getLatLongRefSource());
			newgeo.setLatLongRemarks(prevgeo.getLatLongRemarks());
			newgeo.setLatMin(prevgeo.getLatMin());
			newgeo.setLatSec(prevgeo.getLatSec());
			newgeo.setLongDeg(prevgeo.getLongDeg());
			newgeo.setLongDir(prevgeo.getLongDir());
			newgeo.setLongMin(prevgeo.getLongMin());
			newgeo.setLongSec(prevgeo.getLongSec());
			newgeo.setMaxErrorDistance(prevgeo.getMaxErrorDistance());
			newgeo.setMaxErrorUnits(prevgeo.getMaxErrorUnits());
			newgeo.setNearestNamedPlace(prevgeo.getNearestNamedPlace());
			newgeo.setOrigLatLongUnits(prevgeo.getOrigLatLongUnits());
			newgeo.setUtmEw(prevgeo.getUtmEw());
			newgeo.setUtmNs(prevgeo.getUtmNs());
			newgeo.setUtmZone(prevgeo.getUtmZone());
			newgeo.setVerificationstatus(prevgeo.getVerificationstatus());			
		}

		
		//new - verbatim locality
		jTextFieldVerbatimLocality.setText(previousSpecimen.getVerbatimLocality());
		//new - publications
		jTextFieldCitedInPub.setText(previousSpecimen.getCitedInPublication());
		//new - features
		jComboBoxFeatures.setSelectedItem(previousSpecimen.getFeatures());
		//new - collecting method
		jTextFieldCollectingMethod.setText(previousSpecimen.getCollectingMethod());
		
		
		setSpecimenPartsTableCellEditors();
	}
	
	//do copy: family, subfamily, tribe, genus, species, subspecies, infra name, rank, author, nature of id, id date, id by 
	private void copyPreviousTaxonRecord(){
		jComboBoxFamily.setSelectedItem(previousSpecimen.getFamily());
		jComboBoxSubfamily.setSelectedItem(previousSpecimen.getSubfamily());
		jTextFieldTribe.setText(previousSpecimen.getTribe());
		jTextFieldGenus.setText(previousSpecimen.getGenus());
		jTextFieldSpecies.setText(previousSpecimen.getSpecificEpithet());
		jTextFieldSubspecies.setText(previousSpecimen.getSubspecificEpithet());
		jTextFieldInfraspecificEpithet.setText(previousSpecimen.getInfraspecificEpithet());
		jTextFieldInfraspecificRank.setText(previousSpecimen.getInfraspecificRank());
		jTextFieldAuthorship.setText(previousSpecimen.getAuthorship());
    	jCBDeterminer.setSelectedItem(previousSpecimen.getIdentifiedBy());
		jComboBoxNatureOfId.setSelectedItem(previousSpecimen.getNatureOfId());
		jTextFieldDateDetermined.setText(previousSpecimen.getDateIdentified());
	}
	
	
	private void setValues() { 
		log.debug("okok copyprevious, specimenid is " + specimen.getSpecimenId());
		log.debug("invoking setValues()");
		jTextFieldStatus.setText("Loading");		
		jTextFieldBarcode.setText(specimen.getBarcode());
		
		//alliefix - set to value from properties
		//jComboBoxLocationInCollection.setSelectedItem(specimen.getLocationInCollection());	
		String locationInCollectionPropertiesVal = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
	    		 ImageCaptureProperties.DISPLAY_COLLECTION);
		jComboBoxLocationInCollection.setSelectedItem(locationInCollectionPropertiesVal);
		
		//allie try
		/*Set<LatLong> georeferences = specimen.getLatLong();
		log.debug("setvalues: georeferences size is : + " + georeferences.size());
		LatLong georeference_pre = georeferences.iterator().next();
		log.debug("lat is : + " + georeference_pre.getLatDegString());
		log.debug("long is : + " + georeference_pre.getLongDegString());*/
		
		cbTypeStatus.setSelectedItem(specimen.getTypeStatus());
		jTextFieldDrawerNumber.setText(specimen.getDrawerNumber());
		jComboBoxFamily.setSelectedItem(specimen.getFamily());
		jComboBoxSubfamily.setSelectedItem(specimen.getSubfamily());
		jTextFieldTribe.setText(specimen.getTribe());
		jTextFieldGenus.setText(specimen.getGenus());
		jTextFieldSpecies.setText(specimen.getSpecificEpithet());
		jTextFieldSubspecies.setText(specimen.getSubspecificEpithet());
		jTextFieldInfraspecificEpithet.setText(specimen.getInfraspecificEpithet());
		jTextFieldInfraspecificRank.setText(specimen.getInfraspecificRank());
		jTextFieldAuthorship.setText(specimen.getAuthorship());
		
		//allie new - bugfix
		microhabitTextField.setText(specimen.getMicrohabitat());
		
		jTextFieldIdRemarks.setText(specimen.getIdentificationRemarks());
		jTextFieldDateDetermined.setText(specimen.getDateIdentified());
		
    	//allie change
    	//log.debug("jComboBoxLifeStage here!!! specimen life stage is " + specimen.getLifeStage());
    	if(specimen.getLifeStage() == null || specimen.getLifeStage().equals("")){
    		specimen.setLifeStage("adult");
    		jComboBoxLifeStage.setSelectedIndex(0);
    	}
    	
		//allie change - removed this 
        //MCZbaseAuthAgentName selection = new MCZbaseAuthAgentName();
        //selection.setAgent_name(specimen.getIdentifiedBy());
        //((AgentNameComboBoxModel)jCBDeterminer.getModel()).setSelectedItem(selection);
		//jCBDeterminer.getEditor().setItem(jCBDeterminer.getModel().getSelectedItem());
        
		//allie change - added this
        //jCBDeterminer.setText(specimen.getIdentifiedBy());
    	jCBDeterminer.setSelectedItem(specimen.getIdentifiedBy());
		
		jComboBoxNatureOfId.setSelectedItem(specimen.getNatureOfId());
		
		jTextFieldUnnamedForm.setText(specimen.getUnNamedForm());
		jTextFieldVerbatimLocality.setText(specimen.getVerbatimLocality());
		// Specimen record contains a string, delegate handling of lookup of object to the combo box model.
		//log.debug(specimen.getHigherGeography());
		
		//allieremove
		//((HigherGeographyComboBoxModel)comboBoxHigherGeog.getModel()).setSelectedItem(specimen.getHigherGeography());
//TODO ? set model not notifying listeners? 		
		//comboBoxHigherGeog.getEditor().setItem(comboBoxHigherGeog.getModel().getSelectedItem());
		
		//jTextFieldCountry.setText(specimen.getCountry());
		jComboBoxCountry.setSelectedItem(specimen.getCountry());
		if (specimen.getValidDistributionFlag()!=null) { 
		    jCheckBoxValidDistributionFlag.setSelected(specimen.getValidDistributionFlag());
		} else { 
			jCheckBoxValidDistributionFlag.setSelected(false);
		}
		jComboBoxPrimaryDivision.setSelectedItem(specimen.getPrimaryDivison());
		jTextFieldLocality.setText(specimen.getSpecificLocality());
		
	    // Elevations  **********************************************************************
		try { 
		    jTextFieldMinElevation.setText(Long.toString(specimen.getMinimum_elevation()));
		} catch (Exception e) { 
		    jTextFieldMinElevation.setText("");
		}
		try { 
		    textFieldMaxElev.setText(Long.toString(specimen.getMaximum_elevation()));
		} catch (Exception e) { 
			textFieldMaxElev.setText("");
		}
		if (specimen.getElev_units()!=null) { 
		    comboBoxElevUnits.setSelectedItem(specimen.getElev_units());
		} else {
			comboBoxElevUnits.setSelectedItem("");
		}

		jTextFieldCollectingMethod.setText(specimen.getCollectingMethod());
		jTextFieldISODate.setText(specimen.getIsoDate());
		jTextFieldDateNos.setText(specimen.getDateNos());
		jTextFieldDateCollected.setText(specimen.getDateCollected());
		jTextFieldDateEmerged.setText(specimen.getDateEmerged());
		jTextFieldDateCollectedIndicator.setText(specimen.getDateCollectedIndicator());
		jTextFieldDateEmergedIndicator.setText(specimen.getDateEmergedIndicator());
		jComboBoxCollection.setSelectedItem(specimen.getCollection());
		//jTextFieldPreparationType.setText(specimen.getPreparationType());
		jTextFieldAssociatedTaxon.setText(specimen.getAssociatedTaxon());
		jTextFieldHabitat.setText(specimen.getHabitat());
		jTextAreaSpecimenNotes.setText(specimen.getSpecimenNotes());
		jComboBoxFeatures.setSelectedItem(specimen.getFeatures());
		jComboBoxLifeStage.setSelectedItem(specimen.getLifeStage());
		jComboBoxSex.setSelectedItem(specimen.getSex());
		jTextFieldCitedInPub.setText(specimen.getCitedInPublication());
		jTextFieldQuestions.setText(specimen.getQuestions());
		jComboBoxWorkflowStatus.setSelectedItem(specimen.getWorkFlowStatus());
	    if (specimen.isStateDone()) { 
	    	jLabelMigrationStatus.setText("http://mczbase.mcz.harvard.edu/guid/MCZ:Ent:" + specimen.getCatNum());
	    } else { 
	    	jLabelMigrationStatus.setText("");
	    }
		jTextFieldInferences.setText(specimen.getInferences());
		jTextFieldCreator.setText(specimen.getCreatedBy());
		if (specimen.getDateCreated()!=null) { 
		   jTextFieldDateCreated.setText(specimen.getDateCreated().toString());
		} 
		jTextFieldLastUpdatedBy.setText(specimen.getLastUpdatedBy());
		if (specimen.getDateLastUpdated()!=null) { 
		   jTextFieldDateLastUpdated.setText(specimen.getDateLastUpdated().toString());
		} 
		
		//allie change
    	if(specimen.getNatureOfId() == null || specimen.getNatureOfId().equals("")){
    		specimen.setLifeStage("expert ID");
    		jComboBoxNatureOfId.setSelectedIndex(0);
    	}
		
		//without this, it does save the 1st record, and it does not copy the next record!
    	log.debug("setValues calling jTableNumbers.setModel(new NumberTableModel(specimen.getNumbers()));");
    	jTableNumbers.setModel(new NumberTableModel(specimen.getNumbers()));

		// Setting the model will overwrite the existing cell editor bound 
		// to the column model, so we need to add it again.
		JTextField field1 = new JTextField();
		field1.setInputVerifier(MetadataRetriever.getInputVerifier(edu.harvard.mcz.imagecapture.data.Number.class, "Number", field1));
		field1.setVerifyInputWhenFocusTarget(true);
		jTableNumbers.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field1));
		JComboBox jComboNumberTypes = new JComboBox();
		jComboNumberTypes.setModel(new DefaultComboBoxModel(NumberLifeCycle.getDistinctTypes()));
		jComboNumberTypes.setEditable(true);
		TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
		typeColumn.setCellEditor(new DefaultCellEditor(jComboNumberTypes));
		
		jTableCollectors.setModel(new CollectorTableModel(specimen.getCollectors()));
		
		
		// Setting the model will overwrite the existing cell editor bound 
		// to the column model, so we need to add it again.
		
		//this line is the original!!!
		//FilteringAgentJComboBox field = new FilteringAgentJComboBox();
		
		//paul's comments
		//field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		//jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new PicklistTableCellEditor(field, true));
		
		//this line is the original!!!
		//jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(field));
		
		//paul's comments
		//field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		//field.setVerifyInputWhenFocusTarget(true);

		//allie - change: this is for simple text fields. below it has changed to combo boxes!
		/*JTextField field = new JTextField();
		field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field));*/
		//end allie change
		
		//allie new
		CollectorLifeCycle cls = new CollectorLifeCycle();
		JComboBox jComboBoxCollector = new JComboBox(cls.getDistinctCollectors());
		jComboBoxCollector.setEditable(true);
		//field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(jComboBoxCollector));
		AutoCompleteDecorator.decorate(jComboBoxCollector);
		//end allie new
		
		jTableSpecimenParts.setModel(new SpecimenPartsTableModel(specimen.getSpecimenParts()));
		jTableSpecimenParts.getColumnModel().getColumn(0).setPreferredWidth(90);
		for (int i = 0; i < jTableSpecimenParts.getColumnCount(); i++) {
		    TableColumn column = jTableSpecimenParts.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setPreferredWidth(120); 
		    } else {
		        column.setPreferredWidth(50);
		    }
		}
		setSpecimenPartsTableCellEditors();
		
		updateDeterminationCount();
		
		if (specimen.getICImages()!=null) { 
			int imageCount = specimen.getICImages().size();
			jTextFieldImageCount.setText("Number of Images="+ imageCount);
			if (imageCount>1) { 
				jTextFieldImageCount.setForeground(Color.RED);
			} else { 
				jTextFieldImageCount.setForeground(Color.BLACK);
			}
		}
		
		setWarnings();
		this.setStateToClean();
		jTextFieldStatus.setText("Loaded");
	}

	private void updateDeterminationCount() {
		if (specimen.getDeterminations()==null) { 
			jButtonDeterminations.setText("0 Dets.");
		} else { 
		    if (specimen.getDeterminations().size()==1) { 
		    	jButtonDeterminations.setText(Integer.toString(specimen.getDeterminations().size()) + " Det.");
		    } else {
			    jButtonDeterminations.setText(Integer.toString(specimen.getDeterminations().size()) + " Dets.");
		    }
		}
	}
	

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldStatus() {
		if (jTextFieldStatus == null) {
			jTextFieldStatus = new JTextField("Status");
			jTextFieldStatus.setEditable(false);
			jTextFieldStatus.setEnabled(true);
		}
		return jTextFieldStatus;
	}

	/**
	 * This method initializes jPanel, laying out the UI components.
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraintsImgCount = new GridBagConstraints();
			gridBagConstraintsImgCount.fill = GridBagConstraints.BOTH;
			gridBagConstraintsImgCount.gridy = 6;
			gridBagConstraintsImgCount.weightx = 1.0;
			gridBagConstraintsImgCount.anchor = GridBagConstraints.WEST;
			gridBagConstraintsImgCount.gridwidth = 4;
			gridBagConstraintsImgCount.insets = new Insets(0, 3, 5, 0);
			gridBagConstraintsImgCount.ipadx = 3;
			gridBagConstraintsImgCount.gridx = 4;
			GridBagConstraints gridBagConstraints124 = new GridBagConstraints();
			gridBagConstraints124.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints124.gridx = 0;
			gridBagConstraints124.anchor = GridBagConstraints.EAST;
			gridBagConstraints124.gridy = 20;
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints38.gridx = 0;
			gridBagConstraints38.anchor = GridBagConstraints.EAST;
			gridBagConstraints38.gridy = 27;
			GridBagConstraints gridBagConstraints215 = new GridBagConstraints();
			gridBagConstraints215.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints215.gridx = 0;
			gridBagConstraints215.anchor = GridBagConstraints.EAST;
			gridBagConstraints215.gridy = 26;
			GridBagConstraints gridBagConstraints120 = new GridBagConstraints();
			gridBagConstraints120.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints120.gridx = 3;
			gridBagConstraints120.weightx = 1.0;
			gridBagConstraints120.gridy = 25;
			jLabel44 = new JLabel();
			jLabel44.setText("yyyy/mm/dd");
			
			GridBagConstraints gridBagConstraints49 = new GridBagConstraints(); //scroll pane specimen notes here!
			gridBagConstraints49.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints49.fill = GridBagConstraints.BOTH;
			gridBagConstraints49.weighty = 1.0;
			gridBagConstraints49.weightx = 1.0;
			gridBagConstraints49.gridx = 1;
			gridBagConstraints49.gridwidth = 7;
			gridBagConstraints49.gridy = 35;
			//gridBagConstraints49.gridheight = 20; //allie!!
			
			GridBagConstraints gridBagConstraints214 = new GridBagConstraints();
			gridBagConstraints214.gridwidth = 3;
			gridBagConstraints214.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints214.anchor = GridBagConstraints.EAST;
			gridBagConstraints214.gridx = 4;
			gridBagConstraints214.gridy = 42;
			gridBagConstraints214.weightx = 0.0;
			gridBagConstraints214.weighty = 0.0;
			gridBagConstraints214.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints119 = new GridBagConstraints();
			gridBagConstraints119.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints119.anchor = GridBagConstraints.WEST;
			gridBagConstraints119.gridx = 7;
			gridBagConstraints119.gridy = 42;
			gridBagConstraints119.weightx = 0.0;
			gridBagConstraints119.weighty = 0.0;
			gridBagConstraints119.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints213 = new GridBagConstraints();
			gridBagConstraints213.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints213.fill = GridBagConstraints.BOTH;
			gridBagConstraints213.gridy = 31;
			gridBagConstraints213.weightx = 0.0;
			gridBagConstraints213.gridwidth = 7;
			gridBagConstraints213.anchor = GridBagConstraints.WEST;
			gridBagConstraints213.gridx = 1;
			GridBagConstraints gridBagConstraints118 = new GridBagConstraints();
			gridBagConstraints118.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints118.gridx = 0;
			gridBagConstraints118.anchor = GridBagConstraints.EAST;
			gridBagConstraints118.gridwidth = 1;
			gridBagConstraints118.gridy = 31;
			jLabel31 = new JLabel();
			jLabel31.setText("Publications");
			GridBagConstraints gridBagConstraints212 = new GridBagConstraints();
			gridBagConstraints212.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints212.gridx = 3;
			gridBagConstraints212.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints212.gridwidth = 4;
			gridBagConstraints212.gridy = 15;
			GridBagConstraints gridBagConstraints117 = new GridBagConstraints();
			gridBagConstraints117.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints117.fill = GridBagConstraints.BOTH;
			gridBagConstraints117.gridy = 25;
			gridBagConstraints117.weightx = 0.0;
			gridBagConstraints117.gridwidth = 4;
			gridBagConstraints117.anchor = GridBagConstraints.WEST;
			gridBagConstraints117.gridx = 4;
			GridBagConstraints gridBagConstraints211 = new GridBagConstraints();
			gridBagConstraints211.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints211.gridx = 3;
			gridBagConstraints211.gridwidth = 4;
			gridBagConstraints211.weighty = 1.0;
			gridBagConstraints211.gridy = 44;
			GridBagConstraints gridBagConstraintsMS = new GridBagConstraints();
			gridBagConstraintsMS.fill = GridBagConstraints.BOTH;
			gridBagConstraintsMS.gridx = 0;
			gridBagConstraintsMS.gridwidth = 8;
			gridBagConstraintsMS.weighty = 1.0;
			gridBagConstraintsMS.gridy = 45;			
			gridBagConstraintsMS.gridx = 0;
			
		
			
			
			GridBagConstraints gridBagConstraintsInfer = new GridBagConstraints();
			gridBagConstraintsInfer.insets = new Insets(0, 0, 0, 0);
			gridBagConstraintsInfer.fill = GridBagConstraints.BOTH;
			gridBagConstraintsInfer.gridy = 36;
			gridBagConstraintsInfer.weightx = 1.0;
			gridBagConstraintsInfer.anchor = GridBagConstraints.WEST;
			gridBagConstraintsInfer.gridwidth = 7;
			gridBagConstraintsInfer.gridx = 1;
			GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
			gridBagConstraints56.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints56.gridx = 0;
			gridBagConstraints56.anchor = GridBagConstraints.EAST;
			gridBagConstraints56.gridy = 36;
			jLabel43 = new JLabel();
			jLabel43.setText("Inferences");
			GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
			gridBagConstraints48.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints48.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints48.gridy = 0;
			gridBagConstraints48.weightx = 1.0;
			gridBagConstraints48.anchor = GridBagConstraints.WEST;
			gridBagConstraints48.gridwidth = 4;
			gridBagConstraints48.gridx = 4;
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints37.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints37.gridx = 3;
			gridBagConstraints37.anchor = GridBagConstraints.EAST;
			gridBagConstraints37.gridy = 0;
			jLabel42 = new JLabel();
			jLabel42.setText("Collection");
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints29.fill = GridBagConstraints.BOTH;
			gridBagConstraints29.gridy = 41;
			gridBagConstraints29.weightx = 1.0;
			gridBagConstraints29.anchor = GridBagConstraints.WEST;
			gridBagConstraints29.gridwidth = 3;
			gridBagConstraints29.gridx = 1;
			GridBagConstraints gridBagConstraints115 = new GridBagConstraints();
			gridBagConstraints115.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints115.gridx = 0;
			gridBagConstraints115.gridy = 41;
			jLabel41 = new JLabel();
			jLabel41.setText("Workflow Status");
			GridBagConstraints gridBagConstraints172 = new GridBagConstraints();
			gridBagConstraints172.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints172.fill = GridBagConstraints.BOTH;
			gridBagConstraints172.gridy = 33;
			gridBagConstraints172.weightx = 1.0;
			gridBagConstraints172.anchor = GridBagConstraints.WEST;
			gridBagConstraints172.gridwidth = 4;
			gridBagConstraints172.gridx = 4;
			GridBagConstraints gridBagConstraints162 = new GridBagConstraints();
			gridBagConstraints162.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints162.fill = GridBagConstraints.BOTH;
			gridBagConstraints162.gridy = 33;
			gridBagConstraints162.weightx = 1.0;
			gridBagConstraints162.anchor = GridBagConstraints.WEST;
			gridBagConstraints162.gridwidth = 2;
			gridBagConstraints162.gridx = 1;
			GridBagConstraints gridBagConstraints142 = new GridBagConstraints();
			gridBagConstraints142.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints142.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints142.gridy = 42;
			gridBagConstraints142.weightx = 1.0;
			gridBagConstraints142.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints142.gridwidth = 3;
			gridBagConstraints142.ipady = 2;
			gridBagConstraints142.gridx = 1;
			GridBagConstraints gridBagConstraints133 = new GridBagConstraints();
			gridBagConstraints133.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints133.gridx = 0;
			gridBagConstraints133.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints133.gridy = 42;
			jLabel40 = new JLabel();
			jLabel40.setText("Questions");
			GridBagConstraints gridBagConstraints123 = new GridBagConstraints();
			gridBagConstraints123.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints123.gridx = 3;
			gridBagConstraints123.anchor = GridBagConstraints.EAST;
			gridBagConstraints123.gridy = 33;
			jLabel39 = new JLabel();
			jLabel39.setText("Habitat");
			GridBagConstraints gridBagConstraints114 = new GridBagConstraints();
			gridBagConstraints114.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints114.gridx = 0;
			gridBagConstraints114.anchor = GridBagConstraints.EAST;
			gridBagConstraints114.gridy = 33;
			jLabel38 = new JLabel();
			jLabel38.setText("Associated Taxon");
			GridBagConstraints gridBagConstraints94 = new GridBagConstraints();
			gridBagConstraints94.gridwidth = 3;
			gridBagConstraints94.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints94.gridx = 4;
			gridBagConstraints94.anchor = GridBagConstraints.WEST;
			gridBagConstraints94.gridy = 18;
			jLabel36 = new JLabel();
			jLabel36.setText("Valid Dist.");
			GridBagConstraints gridBagConstraints83 = new GridBagConstraints();
			gridBagConstraints83.gridx = 3;
			gridBagConstraints83.anchor = GridBagConstraints.EAST;
			gridBagConstraints83.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints83.weightx = 1.0;
			gridBagConstraints83.gridy = 18;
			GridBagConstraints gridBagConstraints65 = new GridBagConstraints();
			gridBagConstraints65.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints65.gridx = 0;
			gridBagConstraints65.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints65.gridy = 35;
			jLabel35 = new JLabel();
			jLabel35.setText("Specimen Notes");
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints47.gridx = 0;
			gridBagConstraints47.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints47.ipady = 12;
			gridBagConstraints47.gridy = 5;
			jLabel34 = new JLabel();
			jLabel34.setText("     ");
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 23;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.anchor = GridBagConstraints.NORTH;
			gridBagConstraints19.gridwidth = 5;
			gridBagConstraints19.gridx = 3;
			
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints18.fill = GridBagConstraints.BOTH;
			gridBagConstraints18.gridy = 19;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridx = 4;
			GridBagConstraints gbc_jLabelElevation = new GridBagConstraints();
			gbc_jLabelElevation.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelElevation.gridx = 3;
			gbc_jLabelElevation.anchor = GridBagConstraints.EAST;
			gbc_jLabelElevation.gridy = 19;
			jLabelElevation = new JLabel();
			jLabelElevation.setText("Elevation");
			jLabel25 = new JLabel();
			jLabel25.setText("Text");
			jLabel26 = new JLabel();
			jLabel26.setText("Text");
			jLabel28 = new JLabel();
			jLabel28.setText("Rank");
			GridBagConstraints gridBagConstraints161 = new GridBagConstraints();
			gridBagConstraints161.gridx = 0;
			GridBagConstraints gridBagConstraints151 = new GridBagConstraints();
			gridBagConstraints151.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints151.fill = GridBagConstraints.BOTH;
			gridBagConstraints151.gridy = 27;
			gridBagConstraints151.weightx = 1.0;
			gridBagConstraints151.gridx = 1;
			GridBagConstraints gridBagConstraints141 = new GridBagConstraints();
			gridBagConstraints141.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints141.fill = GridBagConstraints.BOTH;
			gridBagConstraints141.gridy = 26;
			gridBagConstraints141.weightx = 1.0;
			gridBagConstraints141.gridx = 1;
			GridBagConstraints gridBagConstraints132 = new GridBagConstraints();
			gridBagConstraints132.gridwidth = 3;
			gridBagConstraints132.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints132.fill = GridBagConstraints.BOTH;
			gridBagConstraints132.gridy = 27;
			gridBagConstraints132.weightx = 1.0;
			gridBagConstraints132.gridx = 4;
			GridBagConstraints gridBagConstraints122 = new GridBagConstraints();
			gridBagConstraints122.gridwidth = 3;
			gridBagConstraints122.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints122.fill = GridBagConstraints.BOTH;
			gridBagConstraints122.gridy = 26;
			gridBagConstraints122.weightx = 1.0;
			gridBagConstraints122.gridx = 4;
			GridBagConstraints gridBagConstraints113 = new GridBagConstraints();
			gridBagConstraints113.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints113.gridx = 3;
			gridBagConstraints113.anchor = GridBagConstraints.EAST;
			gridBagConstraints113.gridy = 27;
			GridBagConstraints gridBagConstraints103 = new GridBagConstraints();
			gridBagConstraints103.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints103.gridx = 3;
			gridBagConstraints103.anchor = GridBagConstraints.EAST;
			gridBagConstraints103.gridy = 26;
			GridBagConstraints gridBagConstraints93 = new GridBagConstraints();
			gridBagConstraints93.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints93.fill = GridBagConstraints.BOTH;
			gridBagConstraints93.gridy = 12;
			gridBagConstraints93.weightx = 1.0;
			gridBagConstraints93.gridx = 1;
			GridBagConstraints gridBagConstraints84 = new GridBagConstraints();
			gridBagConstraints84.gridwidth = 3;
			gridBagConstraints84.fill = GridBagConstraints.BOTH;
			gridBagConstraints84.gridy = 12;
			gridBagConstraints84.weightx = 1.0;
			gridBagConstraints84.anchor = GridBagConstraints.WEST;
			gridBagConstraints84.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints84.gridx = 4;
			GridBagConstraints gridBagConstraints74 = new GridBagConstraints();
			gridBagConstraints74.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints74.gridx = 3;
			gridBagConstraints74.anchor = GridBagConstraints.EAST;
			gridBagConstraints74.weightx = 0.0;
			gridBagConstraints74.gridy = 12;
//			GridBagConstraints gridBagConstraints64 = new GridBagConstraints();
//			gridBagConstraints64.fill = GridBagConstraints.BOTH;
//			gridBagConstraints64.gridy = 14;
//			gridBagConstraints64.weightx = 1.0;
//			gridBagConstraints64.gridx = 3;
			GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
			gridBagConstraints55.gridx = 2;
			gridBagConstraints55.gridy = 14;
			//jLabel31 = new JLabel();
			//jLabel31.setText("Qual.");
			GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
			gridBagConstraints46.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints46.fill = GridBagConstraints.BOTH;
			gridBagConstraints46.gridy = 14;
			gridBagConstraints46.weightx = 1.0;
			gridBagConstraints46.anchor = GridBagConstraints.WEST;
			gridBagConstraints46.gridx = 1;
			gridBagConstraints46.gridwidth = 2;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints36.gridx = 0;
			gridBagConstraints36.anchor = GridBagConstraints.EAST;
			gridBagConstraints36.gridy = 14;
			jLabel30 = new JLabel();
			jLabel30.setText("Unnamed Form");
			GridBagConstraints gridBagConstraints210 = new GridBagConstraints();
			gridBagConstraints210.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints210.fill = GridBagConstraints.BOTH;
			gridBagConstraints210.gridy = 13;
			gridBagConstraints210.weightx = 1.0;
			gridBagConstraints210.anchor = GridBagConstraints.WEST;
			gridBagConstraints210.gridwidth = 2;
			gridBagConstraints210.gridx = 1;
			GridBagConstraints gridBagConstraints112 = new GridBagConstraints();
			gridBagConstraints112.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints112.gridx = 0;
			gridBagConstraints112.anchor = GridBagConstraints.EAST;
			gridBagConstraints112.gridy = 13;
			jLabel29 = new JLabel();
			jLabel29.setText("Author");
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.gridy = 12;
			jLabel27 = new JLabel();
			jLabel27.setText("Infrasubspecifc Name");
			GridBagConstraints gridBagConstraints63 = new GridBagConstraints();
			gridBagConstraints63.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints63.fill = GridBagConstraints.BOTH;
			gridBagConstraints63.gridy = 25;
			gridBagConstraints63.weightx = 1.0;
			gridBagConstraints63.anchor = GridBagConstraints.WEST;
			gridBagConstraints63.gridx = 1;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.anchor = GridBagConstraints.EAST;
			gridBagConstraints35.gridy = 25;
			jLabel22 = new JLabel();
			jLabel22.setText("Verbatim Date");
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints27.fill = GridBagConstraints.BOTH;
			gridBagConstraints27.gridy = 30;
			gridBagConstraints27.weightx = 1.0;
			gridBagConstraints27.anchor = GridBagConstraints.WEST;
			gridBagConstraints27.gridx = 1;
			GridBagConstraints gridBagConstraints102 = new GridBagConstraints();
			gridBagConstraints102.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints102.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints102.gridy = 30;
			gridBagConstraints102.weightx = 1.0;
			gridBagConstraints102.anchor = GridBagConstraints.WEST;
			gridBagConstraints102.gridwidth = 4;
			gridBagConstraints102.gridx = 4;
			GridBagConstraints gridBagConstraints92 = new GridBagConstraints();
			gridBagConstraints92.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints92.gridx = 3;
			gridBagConstraints92.anchor = GridBagConstraints.EAST;
			gridBagConstraints92.gridy = 30;
			jLabel21 = new JLabel();
			jLabel21.setText("Sex");
			GridBagConstraints gridBagConstraints82 = new GridBagConstraints();
			gridBagConstraints82.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints82.gridx = 0;
			gridBagConstraints82.anchor = GridBagConstraints.EAST;
			gridBagConstraints82.gridy = 30;
			jLabel20 = new JLabel();
			jLabel20.setText("LifeStage");
			GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
			gridBagConstraints62.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints62.fill = GridBagConstraints.BOTH;
			gridBagConstraints62.gridy = 7;
			gridBagConstraints62.weightx = 1.0;
			gridBagConstraints62.anchor = GridBagConstraints.WEST;
			gridBagConstraints62.gridwidth = 4;
			gridBagConstraints62.gridx = 4;
			GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
			gridBagConstraints53.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints53.fill = GridBagConstraints.BOTH;
			gridBagConstraints53.gridy = 7;
			gridBagConstraints53.weightx = 1.0;
			gridBagConstraints53.anchor = GridBagConstraints.WEST;
			gridBagConstraints53.gridwidth = 1;
			gridBagConstraints53.gridx = 1;
			GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
			gridBagConstraints44.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints44.fill = GridBagConstraints.BOTH;
			gridBagConstraints44.gridy = 6;
			gridBagConstraints44.weightx = 1.0;
			gridBagConstraints44.anchor = GridBagConstraints.WEST;
			gridBagConstraints44.gridwidth = 3;
			gridBagConstraints44.gridx = 1;
			GridBagConstraints gbc_jLabelTribe = new GridBagConstraints();
			gbc_jLabelTribe.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelTribe.gridx = 3;
			gbc_jLabelTribe.anchor = GridBagConstraints.EAST;
			gbc_jLabelTribe.gridy = 7;
			jLabelTribe = new JLabel();
			jLabelTribe.setText("Tribe");
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.anchor = GridBagConstraints.EAST;
			gridBagConstraints26.gridy = 7;
			jLabel17 = new JLabel();
			jLabel17.setText("Subfamily");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.anchor = GridBagConstraints.EAST;
			gridBagConstraints16.gridy = 6;
			jLabel16 = new JLabel();
			jLabel16.setText("Family");
			GridBagConstraints gridBagConstraints131 = new GridBagConstraints();
			gridBagConstraints131.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints131.fill = GridBagConstraints.BOTH;
			gridBagConstraints131.gridy = 19;
			gridBagConstraints131.weightx = 1.0;
			gridBagConstraints131.anchor = GridBagConstraints.WEST;
			gridBagConstraints131.gridwidth = 2;
			gridBagConstraints131.gridx = 1;
			GridBagConstraints gridBagConstraints121 = new GridBagConstraints();
			gridBagConstraints121.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints121.fill = GridBagConstraints.BOTH;
			gridBagConstraints121.gridy = 18;
			gridBagConstraints121.weightx = 1.0;
			gridBagConstraints121.anchor = GridBagConstraints.WEST;
			gridBagConstraints121.gridwidth = 1;
			gridBagConstraints121.gridx = 1;
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints111.fill = GridBagConstraints.BOTH;
			gridBagConstraints111.gridy = 16;
			gridBagConstraints111.weightx = 1.0;
			gridBagConstraints111.anchor = GridBagConstraints.WEST;
			gridBagConstraints111.gridwidth = 7;
			gridBagConstraints111.gridx = 1;
			GridBagConstraints gridBagConstraints101 = new GridBagConstraints();
			gridBagConstraints101.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints101.gridx = 0;
			gridBagConstraints101.anchor = GridBagConstraints.EAST;
			gridBagConstraints101.gridy = 19;
			jLabel15 = new JLabel();
			jLabel15.setText("State/Province");
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints91.gridx = 0;
			gridBagConstraints91.anchor = GridBagConstraints.EAST;
			gridBagConstraints91.gridy = 18;
			jLabel14 = new JLabel();
			//Change this to dropdown
			jLabel14.setText("Country");
			GridBagConstraints gridBagConstraints81 = new GridBagConstraints();
			gridBagConstraints81.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints81.gridx = 0;
			gridBagConstraints81.anchor = GridBagConstraints.EAST;
			gridBagConstraints81.gridy = 16;
			jLabel13 = new JLabel();
			jLabel13.setText("Verbatim Locality");
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints71.fill = GridBagConstraints.BOTH;
			gridBagConstraints71.gridy = 15;
			gridBagConstraints71.weightx = 1.0;
			gridBagConstraints71.anchor = GridBagConstraints.WEST;
			gridBagConstraints71.gridx = 1;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.anchor = GridBagConstraints.EAST;
			gridBagConstraints61.gridy = 15;
			jLabel12 = new JLabel();
			jLabel12.setText("DrawerNumber");
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints52.fill = GridBagConstraints.BOTH;
			gridBagConstraints52.gridy = 43;
			gridBagConstraints52.weightx = 1.0;
			gridBagConstraints52.weighty = 1.0;
			gridBagConstraints52.gridwidth = 8;
			gridBagConstraints52.gridx = 0;
			GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
			gridBagConstraints43.gridheight = 2;
			gridBagConstraints43.gridx = 0;
			gridBagConstraints43.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints43.ipady = 0;
			gridBagConstraints43.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints43.gridy = 23;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints33.gridy = 4;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints25.fill = GridBagConstraints.BOTH;
			gridBagConstraints25.gridy = 3;
			gridBagConstraints25.weightx = 1.0;
			gridBagConstraints25.weighty = 0.0;
			gridBagConstraints25.gridheight = 3;
			gridBagConstraints25.gridwidth = 6;
			gridBagConstraints25.gridx = 1;
			
			//copy prev
			GridBagConstraints gridBagConstraints116 = new GridBagConstraints();
			gridBagConstraints116.gridwidth = 3;
			gridBagConstraints116.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints116.gridx = 4;
			gridBagConstraints116.anchor = GridBagConstraints.EAST;
			gridBagConstraints116.gridy = 41;
			
			GridBagConstraints gridBagConstraintsCopyPrev = new GridBagConstraints();
			gridBagConstraintsCopyPrev.gridwidth = 5;
			gridBagConstraintsCopyPrev.insets = new Insets(0, 0, 0, 5);
			gridBagConstraintsCopyPrev.anchor = GridBagConstraints.WEST;
			gridBagConstraintsCopyPrev.gridy = 1;
			gridBagConstraintsCopyPrev.gridx = 1;
			
			/*GridBagConstraints gridBagConstraintsCopyPrevTaxon = new GridBagConstraints();
			gridBagConstraintsCopyPrev.gridwidth = 5;
			gridBagConstraintsCopyPrev.insets = new Insets(0, 0, 0, 5);
			gridBagConstraintsCopyPrev.anchor = GridBagConstraints.WEST;
			gridBagConstraintsCopyPrev.gridy = 400;
			gridBagConstraintsCopyPrev.gridx = 1;*/
			//end copy prev
			
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints15.gridy = 3;
			jLabel11 = new JLabel();
			jLabel11.setText("Numbers");
			GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
			gridBagConstraints42.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints42.gridx = 3;
			gridBagConstraints42.anchor = GridBagConstraints.EAST;
			gridBagConstraints42.gridy = 37;
			jLabel10 = new JLabel();
			jLabel10.setText("Date Created");
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints32.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints32.gridy = 37;
			gridBagConstraints32.weightx = 1.0;
			gridBagConstraints32.anchor = GridBagConstraints.WEST;
			gridBagConstraints32.gridwidth = 4;
			gridBagConstraints32.gridx = 4;
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints24.gridx = 0;
			gridBagConstraints24.anchor = GridBagConstraints.EAST;
			gridBagConstraints24.gridy = 37;
			jLabel9 = new JLabel();
			jLabel9.setText("CreatedBy");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 37;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridy = 39;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.gridwidth = 4;
			gridBagConstraints23.gridx = 4;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints13.gridx = 3;
			gridBagConstraints13.anchor = GridBagConstraints.EAST;
			gridBagConstraints13.gridy = 39;
			jLabel8 = new JLabel();
			jLabel8.setText("Last Updated");
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints22.gridy = 22;
			jLabel7 = new JLabel();
			jLabel7.setText("Collectors");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 39;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints51.fill = GridBagConstraints.BOTH;
			gridBagConstraints51.gridy = 22;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.anchor = GridBagConstraints.WEST;
			gridBagConstraints51.gridheight = 3;
			gridBagConstraints51.gridwidth = 2;
			gridBagConstraints51.gridx = 1;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = GridBagConstraints.NONE;
			gridBagConstraints41.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints41.gridy = 39;
			jLabel6 = new JLabel();
			jLabel6.setText("LastUpdatedBy");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.gridy = 21;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridwidth = 6;
			gridBagConstraints31.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.SOUTHEAST;
			gridBagConstraints21.gridy = 21;
			jLabel5 = new JLabel();
			jLabel5.setText("Collection");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints10.gridx = 7;
			gridBagConstraints10.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints10.gridy = 41;
			gridBagConstraints10.weighty = 0.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.EAST;
			gridBagConstraints9.gridy = 11;
			jLabel4 = new JLabel();
			jLabel4.setText("Subspecies");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridy = 10;
			jLabel3 = new JLabel();
			jLabel3.setText("Species");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 20;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridwidth = 7;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 11;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 10;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 9;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 9;
			jLabel2 = new JLabel();
			jLabel2.setText("Genus");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Barcode");
			jPanel = new JPanel();
			GridBagLayout gbl_jPanel = new GridBagLayout();
			gbl_jPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			gbl_jPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			gbl_jPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
			gbl_jPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0};
			jPanel.setLayout(gbl_jPanel);
			jPanel.add(jLabel, gridBagConstraints);
			jPanel.add(getJTextFieldBarcode(), gridBagConstraints1);
			jPanel.add(jLabel2, gridBagConstraints2);
			jPanel.add(getJTextField1(), gridBagConstraints3);
			GridBagConstraints gbc_lblNatureofid = new GridBagConstraints();
			gbc_lblNatureofid.anchor = GridBagConstraints.EAST;
			gbc_lblNatureofid.insets = new Insets(0, 0, 0, 5);
			gbc_lblNatureofid.gridx = 3;
			gbc_lblNatureofid.gridy = 9;
			jPanel.add(getLblNatureofid(), gbc_lblNatureofid);
			
			jComboBoxNatureOfId = getJComboBoxNatureOfId();
			GridBagConstraints gbc_jTextFieldNatureOfID = new GridBagConstraints();
			gbc_jTextFieldNatureOfID.gridwidth = 4;
			gbc_jTextFieldNatureOfID.insets = new Insets(0, 0, 0, 0);
			gbc_jTextFieldNatureOfID.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldNatureOfID.gridx = 4;
			gbc_jTextFieldNatureOfID.gridy = 9;
			jPanel.add(jComboBoxNatureOfId, gbc_jTextFieldNatureOfID);
			jPanel.add(getJTextField12(), gridBagConstraints4);
			GridBagConstraints gbc_lblIdDate = new GridBagConstraints();
			gbc_lblIdDate.anchor = GridBagConstraints.EAST;
			gbc_lblIdDate.insets = new Insets(0, 0, 0, 5);
			gbc_lblIdDate.gridx = 3;
			gbc_lblIdDate.gridy = 10;
			jPanel.add(getLblIdDate(), gbc_lblIdDate);
			
			jTextFieldDateDetermined = getJTextFieldDateDetermined();
			GridBagConstraints gbc_jTextFieldDateDetermined = new GridBagConstraints();
			gbc_jTextFieldDateDetermined.gridwidth = 4;
			gbc_jTextFieldDateDetermined.insets = new Insets(0, 0, 0, 0);
			gbc_jTextFieldDateDetermined.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldDateDetermined.gridx = 4;
			gbc_jTextFieldDateDetermined.gridy = 10;
			jPanel.add(jTextFieldDateDetermined, gbc_jTextFieldDateDetermined);
			jTextFieldDateDetermined.setColumns(10);
			jPanel.add(getJTextField2(), gridBagConstraints5);
			
			JLabel lblIdBy = new JLabel("Id By");
			GridBagConstraints gbc_lblIdBy = new GridBagConstraints();
			gbc_lblIdBy.anchor = GridBagConstraints.EAST;
			gbc_lblIdBy.insets = new Insets(0, 0, 0, 5);
			gbc_lblIdBy.gridx = 3;
			gbc_lblIdBy.gridy = 11;
			jPanel.add(lblIdBy, gbc_lblIdBy);
			
			//jCBDeterminer = getJCBDeterminer();
			GridBagConstraints gbc_jTextFieldDeterminer = new GridBagConstraints();
			gbc_jTextFieldDeterminer.gridwidth = 4;
			gbc_jTextFieldDeterminer.insets = new Insets(0, 0, 0, 0);
			gbc_jTextFieldDeterminer.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldDeterminer.gridx = 4;
			gbc_jTextFieldDeterminer.gridy = 11;
			jPanel.add(getJCBDeterminer(), gbc_jTextFieldDeterminer);
			
			GridBagConstraints gbc_lblIdRemarks = new GridBagConstraints();
			gbc_lblIdRemarks.anchor = GridBagConstraints.EAST;
			gbc_lblIdRemarks.insets = new Insets(0, 0, 0, 5);
			gbc_lblIdRemarks.gridx = 3;
			gbc_lblIdRemarks.gridy = 13;
			jPanel.add(getLblIdRemarks(), gbc_lblIdRemarks);
			
			jTextFieldIdRemarks = getJTextFieldIdRemarks();
			GridBagConstraints gbc_jTextFieldIdRemarks = new GridBagConstraints();
			gbc_jTextFieldIdRemarks.insets = new Insets(0, 0, 0, 0);
			gbc_jTextFieldIdRemarks.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldIdRemarks.gridx = 4;
			gbc_jTextFieldIdRemarks.gridy = 13;
			gbc_jTextFieldIdRemarks.gridwidth = 4;
			jPanel.add(jTextFieldIdRemarks, gbc_jTextFieldIdRemarks);
			jTextFieldIdRemarks.setColumns(10);
			GridBagConstraints gbc_lblTypestatus = new GridBagConstraints();
			gbc_lblTypestatus.anchor = GridBagConstraints.EAST;
			gbc_lblTypestatus.insets = new Insets(0, 0, 0, 5);
			gbc_lblTypestatus.gridx = 3;
			gbc_lblTypestatus.gridy = 14;
			jPanel.add(getLblTypestatus(), gbc_lblTypestatus);
			cbTypeStatus = getCbTypeStatus();
			GridBagConstraints gbc_cbTypeStatus = new GridBagConstraints();
			gbc_cbTypeStatus.gridwidth = 4;
			gbc_cbTypeStatus.insets = new Insets(0, 0, 0, 5);
			gbc_cbTypeStatus.fill = GridBagConstraints.HORIZONTAL;
			gbc_cbTypeStatus.gridx = 4;
			gbc_cbTypeStatus.gridy = 14;
			jPanel.add(cbTypeStatus, gbc_cbTypeStatus);
			//allieremove
			/*GridBagConstraints gbc_lblHigherGeography = new GridBagConstraints();
			gbc_lblHigherGeography.anchor = GridBagConstraints.EAST;
			gbc_lblHigherGeography.insets = new Insets(0, 0, 0, 5);
			gbc_lblHigherGeography.gridx = 0;
			gbc_lblHigherGeography.gridy = 17;
			//jPanel.add(getLblHigherGeography(), gbc_lblHigherGeography);
			GridBagConstraints gbc_comboBoxElevUnits = new GridBagConstraints();
			gbc_comboBoxElevUnits.insets = new Insets(0, 0, 0, 0);
			gbc_comboBoxElevUnits.gridwidth = 7;
			gbc_comboBoxElevUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxElevUnits.gridx = 1;
			gbc_comboBoxElevUnits.gridy = 17;
			//jPanel.add(getComboBoxHighGeog(), gbc_comboBoxElevUnits);*/
			GridBagConstraints gbc_lblTo = new GridBagConstraints();
			gbc_lblTo.insets = new Insets(0, 0, 0, 5);
			gbc_lblTo.anchor = GridBagConstraints.EAST;
			gbc_lblTo.gridx = 5;
			gbc_lblTo.gridy = 19;
			jPanel.add(getLblTo(), gbc_lblTo);
			GridBagConstraints gbc_textFieldMaxElev = new GridBagConstraints();
			gbc_textFieldMaxElev.insets = new Insets(0, 0, 0, 5);
			gbc_textFieldMaxElev.fill = GridBagConstraints.BOTH;
			gbc_textFieldMaxElev.gridx = 6;
			gbc_textFieldMaxElev.gridy = 19;
			jPanel.add(getTextFieldMaxElev(), gbc_textFieldMaxElev);
			GridBagConstraints gbc_comboBoxMaxElev = new GridBagConstraints();
			gbc_comboBoxMaxElev.insets = new Insets(0, 0, 0, 0);
			gbc_comboBoxMaxElev.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxMaxElev.gridx = 7;
			gbc_comboBoxMaxElev.gridy = 19;
			jPanel.add(getComboBoxElevUnits(), gbc_comboBoxMaxElev);
			jPanel.add(getJTextField3(), gridBagConstraints7);
			jPanel.add(jLabel3, gridBagConstraints8);
			jPanel.add(jLabel4, gridBagConstraints9);
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.gridwidth = 4;
			gridBagConstraints28.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints28.gridx = 3;
			gridBagConstraints28.anchor = GridBagConstraints.WEST;
			gridBagConstraints28.fill = GridBagConstraints.NONE;
			gridBagConstraints28.gridy = 22;
			jLabel33 = new JLabel();
			jLabel33.setText("Collecting Method");
			jPanel.add(jLabel33, gridBagConstraints28);
			GridBagConstraints gbc_georef = new GridBagConstraints();
			gbc_georef.insets = new Insets(0, 0, 0, 0);
			gbc_georef.gridwidth = 5;
			gbc_georef.fill = GridBagConstraints.HORIZONTAL;
			gbc_georef.gridx = 3;
			gbc_georef.gridy = 24;
			GridBagConstraints gridBagConstraints72 = new GridBagConstraints();
			gridBagConstraints72.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints72.gridx = 0;
			gridBagConstraints72.anchor = GridBagConstraints.EAST;
			gridBagConstraints72.gridy = 28;
			jLabel19 = new JLabel();
			jLabel19.setText("Features");
			jPanel.add(jLabel19, gridBagConstraints72);
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints17.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints17.gridy = 28;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridx = 1;
			jPanel.add(getJComboBoxFeatures(), gridBagConstraints17);
			GridBagConstraints gridBagConstraints104 = new GridBagConstraints();
			gridBagConstraints104.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints104.gridx = 3;
			gridBagConstraints104.anchor = GridBagConstraints.EAST;
			gridBagConstraints104.gridy = 28;
			jLabel37 = new JLabel();
			jLabel37.setText("Prep Type");
			jPanel.add(jLabel37, gridBagConstraints104);
			GridBagConstraints gridBagConstraints152 = new GridBagConstraints();
			gridBagConstraints152.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints152.fill = GridBagConstraints.BOTH;
			gridBagConstraints152.gridy = 28;
			gridBagConstraints152.weightx = 1.0;
			gridBagConstraints152.anchor = GridBagConstraints.WEST;
			gridBagConstraints152.gridwidth = 4;
			gridBagConstraints152.gridx = 4;
			jPanel.add(getJTextFieldPrepType(), gridBagConstraints152);
			
			GridBagConstraints gridBagConstraintsPR = new GridBagConstraints();
			gridBagConstraintsPR.insets = new Insets(0, 0, 0, 0);
			gridBagConstraintsPR.gridx = 0;
			gridBagConstraintsPR.fill = GridBagConstraints.BOTH;
			gridBagConstraintsPR.gridwidth = 8;
			gridBagConstraintsPR.weighty = 1.0;
			gridBagConstraintsPR.gridy = 29;	
			
			jPanel.add(getJScrollPaneSpecimenParts(),gridBagConstraintsPR);
			
			GridBagConstraints gbc_lblMicrohabitat = new GridBagConstraints();
			gbc_lblMicrohabitat.anchor = GridBagConstraints.EAST;
			gbc_lblMicrohabitat.insets = new Insets(0, 0, 0, 5);
			gbc_lblMicrohabitat.gridx = 0;
			gbc_lblMicrohabitat.gridy = 34;
			
			jPanel.add(getLblMicrohabitat(), gbc_lblMicrohabitat);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 34;
			jPanel.add(getMicrohabitTextField(), gbc_textField);
			jPanel.add(getJButton(), gridBagConstraints10);
			jPanel.add(jLabel5, gridBagConstraints21);
			jPanel.add(getJTextFieldCollection(), gridBagConstraints31);
			jPanel.add(jLabel6, gridBagConstraints41);
			jPanel.add(getJTextField14(), gridBagConstraints12);
			jPanel.add(getJScrollPaneCollectors(), gridBagConstraints51);
			jPanel.add(jLabel7, gridBagConstraints22);
			jPanel.add(jLabel8, gridBagConstraints13);
			jPanel.add(getJTextFieldDateUpdated(), gridBagConstraints23);
			jPanel.add(getJTextField22(), gridBagConstraints14);
			jPanel.add(jLabel9, gridBagConstraints24);
			jPanel.add(getJTextField32(), gridBagConstraints32);
			jPanel.add(jLabel10, gridBagConstraints42);
			jPanel.add(jLabel11, gridBagConstraints15);
			jPanel.add(getJScrollPaneNumbers(), gridBagConstraints25);
			jPanel.add(getJButtonNumbersAdd(), gridBagConstraints33);
			jPanel.add(getJButtonCollsAdd(), gridBagConstraints43);
			jPanel.add(getJScrollPaneWarn(), gridBagConstraints52);
			jPanel.add(jLabel12, gridBagConstraints61);
			jPanel.add(getJTextField(), gridBagConstraints71);
			jPanel.add(jLabel13, gridBagConstraints81);
			jPanel.add(jLabel14, gridBagConstraints91);
			jPanel.add(jLabel15, gridBagConstraints101);
			jPanel.add(getJTextField4(), gridBagConstraints111);
			jPanel.add(getJTextField13(), gridBagConstraints121);
			jPanel.add(getJTextField23(), gridBagConstraints131);
			jPanel.add(jLabel16, gridBagConstraints16);
			jPanel.add(jLabel17, gridBagConstraints26);
			jPanel.add(jLabelTribe, gbc_jLabelTribe);
			jPanel.add(getJTextField5(), gridBagConstraints44);
			jPanel.add(getJTextFieldSubfamily(), gridBagConstraints53);
			jPanel.add(getJTextFieldTribe(), gridBagConstraints62);
			jPanel.add(jLabel20, gridBagConstraints82);
			jPanel.add(jLabel21, gridBagConstraints92);
			jPanel.add(getJComboBoxSex(), gridBagConstraints102);
			jPanel.add(getJComboBoxLifeStage(), gridBagConstraints27);
			jPanel.add(jLabel22, gridBagConstraints35);
			jPanel.add(getJTextFieldVerbatimDate(), gridBagConstraints63);
			jPanel.add(jLabel27, gridBagConstraints110);
			jPanel.add(jLabel29, gridBagConstraints112);
			jPanel.add(getJTextFieldAuthorship(), gridBagConstraints210);
			jPanel.add(jLabel30, gridBagConstraints36);
			jPanel.add(getJTextFieldUnnamedForm(), gridBagConstraints46);
			jPanel.add(jLabel28, gridBagConstraints74);
			jPanel.add(getJTextFieldInfraspecificRank(), gridBagConstraints84);
			jPanel.add(getJTextFieldInfraspecificName(), gridBagConstraints93);
			jPanel.add(jLabel25, gridBagConstraints103);
			jPanel.add(jLabel26, gridBagConstraints113);
			jPanel.add(getJTextFieldDateEmergedIndicator(), gridBagConstraints122);
			jPanel.add(getJTextFieldDateCollectedIndicator(), gridBagConstraints132);
			jPanel.add(getJTextFieldDateEmerged(), gridBagConstraints141);
			jPanel.add(getJTextFieldDateCollected(), gridBagConstraints151);
			jPanel.add(jLabelElevation, gbc_jLabelElevation);
			jPanel.add(getJTextField11(), gridBagConstraints18);
			jPanel.add(getJTextFieldCollectingMethod(), gridBagConstraints19);	
            jPanel.add(getJButtonGeoreference(), gbc_georef);		
			jPanel.add(jLabel34, gridBagConstraints47);
			jPanel.add(jLabel35, gridBagConstraints65);
			jPanel.add(getJCheckBox(), gridBagConstraints83);
			jPanel.add(jLabel36, gridBagConstraints94);
			jPanel.add(jLabel38, gridBagConstraints114);
			jPanel.add(jLabel39, gridBagConstraints123);
			jPanel.add(jLabel40, gridBagConstraints133);
			jPanel.add(getJTextField20(), gridBagConstraints142);
			jPanel.add(getJTextField26(), gridBagConstraints162);
			jPanel.add(getJTextFieldHabitat(), gridBagConstraints172);
			jPanel.add(jLabel41, gridBagConstraints115);
			jPanel.add(getJComboBoxWorkflowStatus(), gridBagConstraints29);
			jPanel.add(jLabel42, gridBagConstraints37);
			jPanel.add(getJComboBox2(), gridBagConstraints48);
			jPanel.add(jLabel43, gridBagConstraints56);
			jPanel.add(getJTextFieldInferences(), gridBagConstraintsInfer);
			jPanel.add(getJButton1(), gridBagConstraints116);
			
			JPanel subPanel = new JPanel();
			//subPanel.add(getJButtonCopyPrevTaxon());
		    subPanel.add(getJButtonCopyPrev());
		    
			//jPanel.add(getJButtonCopyPrev(), gridBagConstraintsCopyPrev);
			jPanel.add(subPanel, gridBagConstraintsCopyPrev);
			//not doing this!
			//jPanel.add(getJButtonCopyPrevTaxon(), gridBagConstraintsCopyPrev);
			jPanel.add(getJPanel1(), gridBagConstraints211);
			jPanel.add(getJTextFieldISODate(), gridBagConstraints117);
			jPanel.add(getJButtonDets(), gridBagConstraints212);
			jPanel.add(jLabel31, gridBagConstraints118);
			jPanel.add(getJTextField9(), gridBagConstraints213);
			jPanel.add(getJButtonNext(), gridBagConstraints119);
			jPanel.add(getJButtonPrevious(), gridBagConstraints214);
			
			//JPanel subPanel2 = new JPanel();
			//subPanel2.add(getJScrollPaneNotes(), gridBagConstraints49);
			//jPanel.add(subPanel2);
			jPanel.add(getJScrollPaneNotes(), gridBagConstraints49);
			
			jPanel.add(jLabel44, gridBagConstraints120);
			jPanel.add(getJButton13(), gridBagConstraints215);
			jPanel.add(getJButton2(), gridBagConstraints38);
			jPanel.add(getJButtonSpecificLocality(), gridBagConstraints124);
			jPanel.add(getJTextFieldImgCount(), gridBagConstraintsImgCount);
			jPanel.add(getJLabelMigrationStatus(),gridBagConstraintsMS);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldBarcode	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldBarcode() {
		if (jTextFieldBarcode == null) {
			jTextFieldBarcode = new JTextField(11);
			jTextFieldBarcode.setEditable(false);
            jTextFieldBarcode.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Barcode"));		
		}
		return jTextFieldBarcode;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextFieldGenus == null) {
			jTextFieldGenus = new JTextField();
			jTextFieldGenus.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Genus", jTextFieldGenus));
			jTextFieldGenus.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Genus"));
			jTextFieldGenus.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldGenus;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField12() {
		if (jTextFieldSpecies == null) {
			jTextFieldSpecies = new JTextField();
			jTextFieldSpecies.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SpecificEpithet", jTextFieldSpecies));
			jTextFieldSpecies.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecificEpithet"));
			jTextFieldSpecies.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldSpecies;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextFieldSubspecies == null) {
			jTextFieldSubspecies = new JTextField();
			jTextFieldSubspecies.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SubspecificEpithet", jTextFieldSubspecies));
			jTextFieldSubspecies.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SubspecificEpithet"));
			jTextFieldSubspecies.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldSubspecies;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextFieldLocality == null) {
			jTextFieldLocality = new JTextField();
			jTextFieldLocality.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SpecificLocality", jTextFieldLocality));
			jTextFieldLocality.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecificLocality"));
			jTextFieldLocality.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldLocality;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("Save");
			if (specimen.isStateDone()) { 
				jButton.setEnabled(false);
				jButton.setText("Migrated " + specimen.getLoadFlags());
			}
			jButton.setMnemonic(KeyEvent.VK_S);
			jButton.setToolTipText("Save changes to this record to the database.  No fields should have red backgrounds before you save.");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try { 
					    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					} catch (Exception ex) { 
						log.error(ex);
					}
					
					save();
					
					//this really does nothing!
					((CollectorTableModel)jTableCollectors.getModel()).fireTableDataChanged();
					((NumberTableModel)jTableNumbers.getModel()).fireTableDataChanged();
					
					
					try { 
					    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} catch (Exception ex) { 
						log.error(ex);
					}
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextFieldCollection() {
		if (jComboBoxCollection == null) {
			log.debug("initt jComboBoxCollection");
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jComboBoxCollection = new JComboBox<String>();
			jComboBoxCollection.setModel(new DefaultComboBoxModel<String>(sls.getDistinctCollections()));
			jComboBoxCollection.setEditable(true);
			//jComboBoxCollection.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Collection", jComboBoxCollection));
			jComboBoxCollection.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Collection"));
			jComboBoxCollection.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxCollection);
		}
		return jComboBoxCollection;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField14() {
		if (jTextFieldLastUpdatedBy == null) {
			jTextFieldLastUpdatedBy = new JTextField();
			jTextFieldLastUpdatedBy.setEditable(false);
			jTextFieldLastUpdatedBy.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "LastUpdatedBy"));
			//jTextFieldLastUpdatedBy.setEnabled(false);
			jTextFieldLastUpdatedBy.setForeground(Color.BLACK);
		}
		return jTextFieldLastUpdatedBy;
	}

	/**
	 * This method initializes jScrollPaneCollectors	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneCollectors() {
		if (jScrollPaneCollectors == null) {
			jScrollPaneCollectors = new JScrollPane();
			jScrollPaneCollectors.setViewportView(getJTableCollectors());
			jScrollPaneCollectors.setPreferredSize(new Dimension(jScrollPaneCollectors.getWidth(), 150));
			jScrollPaneCollectors.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneCollectors;
	}

	/**
	 * This method initializes jTableCollectors	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableCollectors() {
		if (jTableCollectors == null) {
			jTableCollectors = new JTable(new CollectorTableModel());
			
			// Note: When setting the values, the table column editor needs to be reset there, as the model is replaced.

			//the original line!!!
			//FilteringAgentJComboBox field = new FilteringAgentJComboBox();
						
			//the original line!!
			//jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(field));
			
			//allie change: this works with text fields only
			/*JTextField field = new JTextField();
			field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
			jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field));*/
			//end allie change
			
			//allie new - combo boxes
			JComboBox field = new JComboBox();
			//field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
			jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(field));
			//
			
			jTableCollectors.setRowHeight(jTableCollectors.getRowHeight()+4);
		    jTableCollectors.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		    
		    jTableCollectors.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnCollsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupCollectors.show(e.getComponent(),e.getX(),e.getY());
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnCollsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupCollectors.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			});
		    
		    jPopupCollectors = new JPopupMenu();
			JMenuItem mntmDeleteRow = new JMenuItem("Delete Row");
			mntmDeleteRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try { 
						log.debug(clickedOnCollsRow);
						if (clickedOnCollsRow>=0) { 
							int ok = JOptionPane.showConfirmDialog(thisPane, "Delete the selected collector?", "Delete Collector", JOptionPane.OK_CANCEL_OPTION);
							if (ok==JOptionPane.OK_OPTION) { 
								log.debug("deleting collectors row " + clickedOnCollsRow);
					            ((CollectorTableModel)jTableCollectors.getModel()).deleteRow(clickedOnCollsRow);
					            setStateToDirty();
							} else { 
								log.debug("collector row delete canceled by user.");
							}
						} else { 
						    JOptionPane.showMessageDialog(thisPane, "Unable to select row to delete.  Try empting the value and pressing Save.");
						}
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(thisPane, "Failed to delete a collector row. " + ex.getMessage());
					}
				}
			});	
			jPopupCollectors.add(mntmDeleteRow);	
		}
		return jTableCollectors;
	}
	
	private JScrollPane getJScrollPaneSpecimenParts() {
		if (jScrollPaneSpecimenParts == null) {
			jScrollPaneSpecimenParts = new JScrollPane();
			jScrollPaneSpecimenParts.setViewportView(getJTableSpecimenParts());
			//jScrollPaneSpecimenParts.setPreferredSize(new Dimension(0, 150));
			//jScrollPaneSpecimenParts.setMinimumSize(new Dimension(0, 100));
			jScrollPaneSpecimenParts.setPreferredSize(new Dimension(0, 75));
			jScrollPaneSpecimenParts.setMinimumSize(new Dimension(0, 75));
			jScrollPaneSpecimenParts.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneSpecimenParts;
	}	
	
	private JTable getJTableSpecimenParts() { 
		if (jTableSpecimenParts == null) {
			try { 
		        jTableSpecimenParts = new JTable(new SpecimenPartsTableModel(specimen.getSpecimenParts()));
		        jTableSpecimenParts.getColumnModel().getColumn(0).setPreferredWidth(90);
			} catch (NullPointerException e) { 
			    jTableSpecimenParts = new JTable(new SpecimenPartsTableModel());
		        jTableSpecimenParts.getColumnModel().getColumn(0).setPreferredWidth(90);
			}
			setSpecimenPartsTableCellEditors();
			
		    log.debug(specimen.getSpecimenParts().size());
		    
		    jTableSpecimenParts.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnPartsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupSpecimenParts.show(e.getComponent(),e.getX(),e.getY());
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnPartsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupSpecimenParts.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			});
		    
		    jPopupSpecimenParts = new JPopupMenu();
			JMenuItem mntmDeleteRow = new JMenuItem("Delete Row");
			mntmDeleteRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try { 
						if (clickedOnPartsRow>=0) { 
							int ok = JOptionPane.showConfirmDialog(thisPane, "Delete the selected preparation?", "Delete Preparation", JOptionPane.OK_CANCEL_OPTION);
							if (ok==JOptionPane.OK_OPTION) { 
								log.debug("deleting parts row " + clickedOnPartsRow);
					            ((SpecimenPartsTableModel)jTableSpecimenParts.getModel()).deleteRow(clickedOnPartsRow);
					            setStateToDirty();
							} else { 
								log.debug("parts row delete canceled by user.");
							}
						} else { 
						    JOptionPane.showMessageDialog(thisPane, "Unable to select row to delete.");
						}
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(thisPane, "Failed to delete a part attribute row. " + ex.getMessage());
					}
				}
			});	
			jPopupSpecimenParts.add(mntmDeleteRow);	
		}
		return jTableSpecimenParts;
	}
	
    private void setSpecimenPartsTableCellEditors() { 
        log.debug("Setting cell editors");
		JComboBox comboBoxPart = new JComboBox(SpecimenPart.PARTNAMES);
		//comboBoxPart.addItem("whole animal");
		//comboBoxPart.addItem("partial animal");
		getJTableSpecimenParts().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBoxPart));
		JComboBox comboBoxPrep = new JComboBox(SpecimenPart.PRESERVENAMES);
		//comboBoxPrep.addItem("pinned");
		//comboBoxPrep.addItem("pointed");
		//comboBoxPrep.addItem("carded");
		//comboBoxPrep.addItem("capsule");
		//comboBoxPrep.addItem("envelope");
		getJTableSpecimenParts().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxPrep));
		
		getJTableSpecimenParts().getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
		getJTableSpecimenParts().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(ButtonEditor.OPEN_SPECIMENPARTATTRIBUTES, this));
	    	
    }

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateUpdated() {
		if (jTextFieldDateLastUpdated == null) {
			jTextFieldDateLastUpdated = new JTextField();
			jTextFieldDateLastUpdated.setEditable(false);
			//jTextFieldDateLastUpdated.setEnabled(false);
			jTextFieldDateLastUpdated.setForeground(Color.BLACK);
		}
		return jTextFieldDateLastUpdated;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField22() {
		if (jTextFieldCreator == null) {
			jTextFieldCreator = new JTextField();
			jTextFieldCreator.setEditable(false);
			//jTextFieldCreator.setEnabled(false);
			jTextFieldCreator.setForeground(Color.BLACK);
			jTextFieldCreator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Creator"));
		}
		return jTextFieldCreator;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField32() {
		if (jTextFieldDateCreated == null) {
			jTextFieldDateCreated = new JTextField();
			jTextFieldDateCreated.setEditable(false);
			//jTextFieldDateCreated.setEnabled(false);
			jTextFieldDateCreated.setForeground(Color.BLACK);
		}
		return jTextFieldDateCreated;
	}

	/**
	 * This method initializes jScrollPaneNumbers	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneNumbers() {
		if (jScrollPaneNumbers == null) {
			jScrollPaneNumbers = new JScrollPane();
			jScrollPaneNumbers.setViewportView(getJTable());
			jScrollPaneNumbers.setPreferredSize(new Dimension(jScrollPaneNumbers.getWidth(),jTextFieldBarcode.getFontMetrics(jTextFieldBarcode.getFont()).getHeight()*6));
			jScrollPaneNumbers.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneNumbers;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTableNumbers == null) {
			jTableNumbers = new JTable(new NumberTableModel());
			JComboBox<String> jComboNumberTypes = new JComboBox<String>();
			jComboNumberTypes.setModel(new DefaultComboBoxModel<String>(NumberLifeCycle.getDistinctTypes()));
			jComboNumberTypes.setEditable(true);
			TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
			DefaultCellEditor comboBoxEditor = new DefaultCellEditor(jComboNumberTypes);
			//TODO: enable autocomplete for numbertypes picklist.
			//AutoCompleteDecorator.decorate((JComboBox) comboBoxEditor.getComponent());
			typeColumn.setCellEditor(comboBoxEditor);
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for pick list of number types.");
            typeColumn.setCellRenderer(renderer);
            jTableNumbers.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
            
            
            jTableNumbers.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnNumsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupNumbers.show(e.getComponent(),e.getX(),e.getY());
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnNumsRow = ((JTable)e.getComponent()).getSelectedRow();
						 jPopupNumbers.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			});
		    
		    jPopupNumbers = new JPopupMenu();
			JMenuItem mntmDeleteRow = new JMenuItem("Delete Row");
			mntmDeleteRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try { 
						if (clickedOnNumsRow>=0) { 
							int ok = JOptionPane.showConfirmDialog(thisPane, "Delete the selected number?", "Delete Number", JOptionPane.OK_CANCEL_OPTION);
							if (ok==JOptionPane.OK_OPTION) { 
								log.debug("deleting numbers row " + clickedOnNumsRow);
					            ((NumberTableModel)jTableNumbers.getModel()).deleteRow(clickedOnNumsRow);
					            setStateToDirty();
							} else { 
								log.debug("number row delete canceled by user.");
							}
						} else { 
						    JOptionPane.showMessageDialog(thisPane, "Unable to select row to delete.  Try empting number and type and pressing Save.");
						}
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(thisPane, "Failed to delete a number row. " + ex.getMessage());
					}
				}
			});	
			jPopupNumbers.add(mntmDeleteRow);
		}
		return jTableNumbers;
	}

	/**
	 * This method initializes jButtonNumbersAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonNumbersAdd() {
		if (jButtonNumbersAdd == null) {
			jButtonNumbersAdd = new JButton();
			jButtonNumbersAdd.setText("+");
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/b_plus.png");
	        try {  
	        	jButtonNumbersAdd.setText("");
	        	jButtonNumbersAdd.setIcon(new ImageIcon(iconFile));
	        	jButtonNumbersAdd.addActionListener(new java.awt.event.ActionListener() {
	        		public void actionPerformed(java.awt.event.ActionEvent e) {
	        			((NumberTableModel)jTableNumbers.getModel()).addNumber(new edu.harvard.mcz.imagecapture.data.Number(specimen, "", ""));
	        			thisPane.setStateToDirty();
	        		}
	        	});
	        } catch (Exception e) { 
			    jButtonNumbersAdd.setText("+");
	        }
		}
		return jButtonNumbersAdd;
	}
	
	private JButton getJButtonGeoreference() {
		if (jButtonGeoreference == null) {
			jButtonGeoreference = new JButton();
			//allie add
			/*try {
				Set<LatLong> georeferences = specimen.getLatLong();
				log.debug("getJButtonGeoreference georeferences size is : + " + georeferences.size());
				LatLong georeference_pre = georeferences.iterator().next();
				log.debug("lat is : + " + georeference_pre.getLatDegString());
				log.debug("long is : + " + georeference_pre.getLongDegString());
				if ((!("").equals(georeference_pre.getLatDegString())) ||
					(!("").equals(georeference_pre.getLongDegString()))){
					jButtonGeoreference.setText("1.0 Georeference");
				}else{
					jButtonGeoreference.setText("0.0 Georeference");
				}
	        } catch (Exception e) { 
	        	log.error(e.getMessage(), e);
	        }	*/	
			
	        try {  
	        	jButtonGeoreference.setText("Georeference");
	        	jButtonGeoreference.addActionListener(new java.awt.event.ActionListener() {
	        		public void actionPerformed(java.awt.event.ActionEvent e) {
	        			thisPane.setStateToDirty();
	        			Set<LatLong> georeferences = specimen.getLatLong();
	        			//log.debug("the lat long is : " + specimen.getLatLong().);
	        			LatLong georeference = georeferences.iterator().next();
	        			georeference.setSpecimenId(specimen);
	        			GeoreferenceDialog georefDialog = new GeoreferenceDialog(georeference);
	        			georefDialog.setVisible(true);
	        		}
	        	});
	        } catch (Exception e) { 
	        	log.error(e.getMessage(), e);
	        }
		}
		return jButtonGeoreference;
	}	

	/**
	 * This method initializes jButtonCollsAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCollsAdd() {
		if (jButtonCollsAdd == null) {
			jButtonCollsAdd = new JButton();
			jButtonCollsAdd.setText("+");
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/b_plus.png");
	        try {  
	        	jButtonCollsAdd.setText("");
	        	jButtonCollsAdd.setIcon(new ImageIcon(iconFile));
	        	jButtonCollsAdd.addActionListener(new java.awt.event.ActionListener() {
	        		public void actionPerformed(java.awt.event.ActionEvent e) {
	        			log.debug("adding a new collector........");
	        			((CollectorTableModel)jTableCollectors.getModel()).addCollector(new Collector(specimen, ""));
	        			thisPane.setStateToDirty();
	        		}
	        	});
	        } catch (Exception e) { 
			    jButtonCollsAdd.setText("+");
	        }
			
		}
		return jButtonCollsAdd;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneWarn() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextPaneWarn());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPaneWarn() {
		if (jTextPaneWarnings == null) {
			jTextPaneWarnings = new JTextPane();
			jTextPaneWarnings.setEditable(false);
			jTextPaneWarnings.setBackground(this.getBackground());
		}
		return jTextPaneWarnings;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldDrawerNumber == null) {
			jTextFieldDrawerNumber = new JTextField();
			jTextFieldDrawerNumber.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DrawerNumber", jTextFieldDrawerNumber));
			jTextFieldDrawerNumber.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DrawerNumber"));
			jTextFieldDrawerNumber.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDrawerNumber;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField4() {
		if (jTextFieldVerbatimLocality == null) {
			jTextFieldVerbatimLocality = new JTextField();
			jTextFieldVerbatimLocality.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "VerbatimLocality", jTextFieldVerbatimLocality));
			jTextFieldVerbatimLocality.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "VerbatimLocality"));
			jTextFieldVerbatimLocality.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldVerbatimLocality;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextField13() {
		//if (jTextFieldCountry == null) {
			/*jTextFieldCountry = new JTextField();
			jTextFieldCountry.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "Country", jTextFieldCountry));
			jTextFieldCountry.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Country"));
			jTextFieldCountry.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});*/
			//allie fix
			if (jComboBoxCountry == null) {
				log.debug("initt jComboBoxCountry");
				SpecimenLifeCycle sls = new SpecimenLifeCycle();
				jComboBoxCountry = new JComboBox<String>();
				//jComboBoxCountry.setModel(new DefaultComboBoxModel<String>(HigherTaxonLifeCycle.selectDistinctSubfamily("")));
				jComboBoxCountry.setModel(new DefaultComboBoxModel<String>(sls.getDistinctCountries()));
				jComboBoxCountry.setEditable(true);
				jComboBoxCountry.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Country"));
				jComboBoxCountry.addKeyListener(new java.awt.event.KeyAdapter() {
					public void keyTyped(java.awt.event.KeyEvent e) {
						thisPane.setStateToDirty();
					}
				});
				AutoCompleteDecorator.decorate(jComboBoxCountry);
			}
		return jComboBoxCountry;
		//return jTextFieldCountry;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	/*private JTextField getJTextField23() {
		if (jTextFieldPrimaryDivision == null) {
			jTextFieldPrimaryDivision = new JTextField();
			jTextFieldPrimaryDivision.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "primaryDivison", jTextFieldPrimaryDivision));
			jTextFieldPrimaryDivision.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "primaryDivison"));
			jTextFieldPrimaryDivision.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldPrimaryDivision;
	}*/
	//allie change
	private JComboBox getJTextField23() {
		if (jComboBoxPrimaryDivision == null) {
			jComboBoxPrimaryDivision = new JComboBox();
			jComboBoxPrimaryDivision.setEditable(true);
			//jComboBoxPrimaryDivision.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "primaryDivison", jTextFieldPrimaryDivision));
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jComboBoxPrimaryDivision.setModel(new DefaultComboBoxModel<String>(sls.getDistinctPrimaryDivisions()));
			jComboBoxPrimaryDivision.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "primaryDivison"));
			jComboBoxPrimaryDivision.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxPrimaryDivision);
		}
		return jComboBoxPrimaryDivision;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextField5() {
		if (jComboBoxFamily == null) {
			jComboBoxFamily = new JComboBox<String>();
			jComboBoxFamily.setModel(new DefaultComboBoxModel<String>(HigherTaxonLifeCycle.selectDistinctFamily()));
			jComboBoxFamily.setEditable(true);
			//jTextFieldFamily.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Family", jTextFieldFamily));
			jComboBoxFamily.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Family"));
			jComboBoxFamily.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxFamily);
		}
		return jComboBoxFamily;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextFieldSubfamily() {
		if (jComboBoxSubfamily == null) {
			jComboBoxSubfamily = new JComboBox<String>();
			jComboBoxSubfamily.setModel(new DefaultComboBoxModel<String>(HigherTaxonLifeCycle.selectDistinctSubfamily("")));
			jComboBoxSubfamily.setEditable(true);
			//jTextFieldSubfamily.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Subfamily", jTextFieldSubfamily));
			jComboBoxSubfamily.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Subfamily"));
			jComboBoxSubfamily.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxSubfamily);
		}
		return jComboBoxSubfamily;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTribe() {
		if (jTextFieldTribe == null) {
			jTextFieldTribe = new JTextField();
			jTextFieldTribe.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Tribe", jTextFieldTribe));
			jTextFieldTribe.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Tribe"));
			jTextFieldTribe.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldTribe;
	}
	
	


	/**
	 * This method initializes jComboBoxSex	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxSex() {
		if (jComboBoxSex == null) {
			jComboBoxSex = new JComboBox<String>();
			jComboBoxSex.setModel(new DefaultComboBoxModel<String>(Sex.getSexValues()));
			jComboBoxSex.setEditable(true);
			jComboBoxSex.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Sex"));
			jComboBoxSex.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxSex);
		}
		return jComboBoxSex;
	}

	/**
	 * This method initializes jComboBoxFeatures	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxFeatures() {
		if (jComboBoxFeatures == null) {
			jComboBoxFeatures = new JComboBox<String>();
			jComboBoxFeatures.setModel(new DefaultComboBoxModel<String>(Features.getFeaturesValues()));
			jComboBoxFeatures.setEditable(true);
			jComboBoxFeatures.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Features"));
			jComboBoxFeatures.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxFeatures);
			// TODO: validate input length 
		}
		return jComboBoxFeatures;
	}
		
	private JComboBox<String> getJComboBoxNatureOfId() {
		if (jComboBoxNatureOfId == null) {
			jComboBoxNatureOfId = new JComboBox<String>();
			jComboBoxNatureOfId.setModel(new DefaultComboBoxModel<String>(NatureOfId.getNatureOfIdValues()));
			jComboBoxNatureOfId.setEditable(false);
			jComboBoxNatureOfId.setToolTipText(MetadataRetriever.getFieldHelp(Determination.class, "NatureOfId"));
			jComboBoxNatureOfId.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxNatureOfId);
			jComboBoxNatureOfId.setSelectedItem(NatureOfId.EXPERT_ID);
			jComboBoxNatureOfId.setSelectedIndex(0);
		}
		return jComboBoxNatureOfId;
	}
		

	/**
	 * This method initializes jComboBoxLifeStage	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getJComboBoxLifeStage() {
		if (jComboBoxLifeStage == null) {
			jComboBoxLifeStage = new JComboBox<String>();
			jComboBoxLifeStage.setModel(new DefaultComboBoxModel<String>(LifeStage.getLifeStageValues()));
			jComboBoxLifeStage.setEditable(true);
			jComboBoxLifeStage.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Lifestage"));
			jComboBoxLifeStage.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxLifeStage);
			jComboBoxLifeStage.setSelectedItem("adult");
			jComboBoxLifeStage.setSelectedIndex(0);
		}
		return jComboBoxLifeStage;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldVerbatimDate() {
		if (jTextFieldDateNos == null) {
			jTextFieldDateNos = new JTextField();
			//jTextFieldDateNos.setToolTipText("Date found on labels where date might be either date collected or date emerged, or some other date");
			jTextFieldDateNos.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "DateNOS", jTextFieldDateNos));
			jTextFieldDateNos.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateNOS"));
			jTextFieldDateNos.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateNos;
	}



	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateEmerged() {
		if (jTextFieldDateEmerged == null) {
			jTextFieldDateEmerged = new JTextField(15);
			jTextFieldDateEmerged.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "DateEmerged", jTextFieldDateEmerged));
			jTextFieldDateEmerged.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateEmerged"));
			jTextFieldDateEmerged.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateEmerged;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateEmergedIndicator() {
		if (jTextFieldDateEmergedIndicator == null) {
			jTextFieldDateEmergedIndicator = new JTextField(15);
			jTextFieldDateEmergedIndicator.setToolTipText("Verbatim text indicating that this is a date emerged.");
			jTextFieldDateEmergedIndicator.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DateEmergedIndicator", jTextFieldDateEmergedIndicator));
			jTextFieldDateEmergedIndicator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateEmergedIndicator"));
			jTextFieldDateEmergedIndicator.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateEmergedIndicator;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateCollected() {
		if (jTextFieldDateCollected == null) {
			jTextFieldDateCollected = new JTextField(15);
			jTextFieldDateCollected.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateCollected"));
			jTextFieldDateCollected.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateCollected;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateCollectedIndicator() {
		if (jTextFieldDateCollectedIndicator == null) {
			jTextFieldDateCollectedIndicator = new JTextField(15);
			jTextFieldDateCollectedIndicator.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DateCollectedIndicator", jTextFieldDateCollectedIndicator));
			jTextFieldDateCollectedIndicator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateCollectedIndicator"));
			jTextFieldDateCollectedIndicator.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateCollectedIndicator;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldInfraspecificName() {
		if (jTextFieldInfraspecificEpithet == null) {
			jTextFieldInfraspecificEpithet = new JTextField(18);
			jTextFieldInfraspecificEpithet.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "InfraspecificEpithet", jTextFieldInfraspecificEpithet));
			jTextFieldInfraspecificEpithet.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "InfraspecificEpithet"));
			jTextFieldInfraspecificEpithet.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInfraspecificEpithet;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldInfraspecificRank() {
		if (jTextFieldInfraspecificRank == null) {
			jTextFieldInfraspecificRank = new JTextField(5);
			jTextFieldInfraspecificRank.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "InfraspecificRank", jTextFieldInfraspecificRank));
			jTextFieldInfraspecificRank.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "InfraspecificRank"));
			jTextFieldInfraspecificRank.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInfraspecificRank;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldAuthorship() {
		if (jTextFieldAuthorship == null) {
			jTextFieldAuthorship = new JTextField();
			jTextFieldAuthorship.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Authorship", jTextFieldAuthorship));
			jTextFieldAuthorship.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Authorship"));
			jTextFieldAuthorship.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldAuthorship;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUnnamedForm() {
		if (jTextFieldUnnamedForm == null) {
			jTextFieldUnnamedForm = new JTextField();
			jTextFieldUnnamedForm.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "UnnamedForm", jTextFieldUnnamedForm));
			jTextFieldUnnamedForm.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "UnnamedForm"));
			jTextFieldUnnamedForm.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldUnnamedForm;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField11() {
		if (jTextFieldMinElevation == null) {
			jTextFieldMinElevation = new JTextField();
			jTextFieldMinElevation.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "VerbatimElevation", jTextFieldMinElevation));
			jTextFieldMinElevation.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "VerbatimElevation"));
			jTextFieldMinElevation.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldMinElevation;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldCollectingMethod() {
		if (jTextFieldCollectingMethod == null) {
			jTextFieldCollectingMethod = new JTextField();
			jTextFieldCollectingMethod.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "CollectingMethod", jTextFieldCollectingMethod));
			jTextFieldCollectingMethod.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "CollectingMethod"));
			jTextFieldCollectingMethod.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldCollectingMethod;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextAreaNotes() {
		if (jTextAreaSpecimenNotes == null) {
			jTextAreaSpecimenNotes = new JTextArea();
			jTextAreaSpecimenNotes.setRows(3);
			jTextAreaSpecimenNotes.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecimenNotes"));
			jTextAreaSpecimenNotes.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextAreaSpecimenNotes;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBoxValidDistributionFlag == null) {
			jCheckBoxValidDistributionFlag = new JCheckBox();
			//jCheckBoxValidDistributionFlag.setToolTipText("Check if locality represents natural biological range.  Uncheck for Specimens that came from a captive breeding program");
			jCheckBoxValidDistributionFlag.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "ValidDistributionFlag"));
			jCheckBoxValidDistributionFlag.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jCheckBoxValidDistributionFlag;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField20() {
		if (jTextFieldQuestions == null) {
			jTextFieldQuestions = new JTextField();
			jTextFieldQuestions.setBackground(MainFrame.BG_COLOR_QC_FIELD);
			jTextFieldQuestions.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "Questions", jTextFieldQuestions));
			jTextFieldQuestions.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Questions"));
			jTextFieldQuestions.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldQuestions;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextFieldPrepType() {
		if (jButtonAddPreparationType == null) {
			jButtonAddPreparationType = new JButton("Add Prep");
			jButtonAddPreparationType.setMnemonic(KeyEvent.VK_P);
			
			jButtonAddPreparationType.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("Adding new SpecimenPart");
					SpecimenPart newPart = new SpecimenPart();
					newPart.setPreserveMethod(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_DEFAULT_PREPARATION));
					newPart.setSpecimenId(specimen);
					SpecimenPartLifeCycle spls = new SpecimenPartLifeCycle();
                    log.debug("Attaching new SpecimenPart");
					try {
						spls.persist(newPart);
					    specimen.getSpecimenParts().add(newPart);
					    ((AbstractTableModel)jTableSpecimenParts.getModel()).fireTableDataChanged();
					    log.debug("Added new SpecimenPart");
					} catch (SaveFailedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});			
		}
		return jButtonAddPreparationType;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField26() {
		if (jTextFieldAssociatedTaxon == null) {
			jTextFieldAssociatedTaxon = new JTextField();
			jTextFieldAssociatedTaxon.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "AssociatedTaxon", jTextFieldAssociatedTaxon));
			jTextFieldAssociatedTaxon.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "AssociatedTaxon"));
			jTextFieldAssociatedTaxon.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldAssociatedTaxon;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldHabitat() {
		if (jTextFieldHabitat == null) {
			jTextFieldHabitat = new JTextField();
			jTextFieldHabitat.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Habitat", jTextFieldHabitat));
			jTextFieldHabitat.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Habitat"));
			jTextFieldHabitat.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldHabitat;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxWorkflowStatus() {
		if (jComboBoxWorkflowStatus == null) {
			jComboBoxWorkflowStatus = new JComboBox<String>();
			jComboBoxWorkflowStatus.setModel(new DefaultComboBoxModel<String>(WorkFlowStatus.getWorkFlowStatusValues()));
			jComboBoxWorkflowStatus.setEditable(false);
			jComboBoxWorkflowStatus.setBackground(MainFrame.BG_COLOR_QC_FIELD);
			jComboBoxWorkflowStatus.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "WorkflowStatus"));
			AutoCompleteDecorator.decorate(jComboBoxWorkflowStatus);
		}
		return jComboBoxWorkflowStatus;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox2() {
		if (jComboBoxLocationInCollection == null) {
			jComboBoxLocationInCollection = new JComboBox<String>();
			jComboBoxLocationInCollection.setModel(new DefaultComboBoxModel<String>(LocationInCollection.getLocationInCollectionValues()));
			jComboBoxLocationInCollection.setEditable(false);
			jComboBoxLocationInCollection.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "LocationInCollection"));
			
			//alliefix - set default from properties file
			//jComboBoxLocationInCollection.setSelectedIndex(1);
			
			jComboBoxLocationInCollection.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxLocationInCollection);
		}
		return jComboBoxLocationInCollection;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldInferences() {
		if (jTextFieldInferences == null) {
			jTextFieldInferences = new JTextField();
			jTextFieldInferences.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
			jTextFieldInferences.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Inferences", jTextFieldInferences));
			jTextFieldInferences.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Inferences"));
			jTextFieldInferences.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInferences;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButtonGetHistory == null) {
			jButtonGetHistory = new JButton();
			jButtonGetHistory.setText("History");
			jButtonGetHistory.setToolTipText("Show the history of who edited this record");
			jButtonGetHistory.setMnemonic(KeyEvent.VK_H);
			jButtonGetHistory.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// retrieve and display the tracking events for this specimen 
					//Tracking t = new Tracking();
					//t.setSpecimen(specimen);
					TrackingLifeCycle tls = new TrackingLifeCycle();
					//Request by specimen doesn't work with Oracle.  Why?  
					//EventLogFrame logViewer = new EventLogFrame(new ArrayList<Tracking>(tls.findBySpecimen(specimen)));
					EventLogFrame logViewer = new EventLogFrame(new ArrayList<Tracking>(tls.findBySpecimenId(specimen.getSpecimenId())));
					logViewer.pack();
					logViewer.setVisible(true);
					
				}
			});
		}
		return jButtonGetHistory;
	}


	private JButton getJButtonCopyPrev() {
		log.debug("prev spec:::");
		if (jButtonCopyPrev == null) {
			jButtonCopyPrev = new JButton();
			jButtonCopyPrev.setText("Copy Prev");
			jButtonCopyPrev.setToolTipText("Copy previous record values into this screen");
			//TODO what is this for???
			//jButtonCopyPrev.setMnemonic(KeyEvent.VK_H);
			jButtonCopyPrev.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//populate the fields with the data.
					previousSpecimen = ImageCaptureApp.lastEditedSpecimenCache;
					copyPreviousRecord();
				}
			});
		}
		return jButtonCopyPrev;
	}
	
	
	private JButton getJButtonCopyPrevTaxon() {
		log.debug("prev taxon:::");
		if (jButtonCopyPrevTaxon == null) {
			jButtonCopyPrevTaxon = new JButton();
			jButtonCopyPrevTaxon.setText("Copy Prev Taxon");
			jButtonCopyPrevTaxon.setToolTipText("Copy previous taxon values into this screen");
			//TODO what is this for???
			//jButtonCopyPrev.setMnemonic(KeyEvent.VK_H);
			jButtonCopyPrevTaxon.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//populate the fields with the data.
					previousSpecimen = ImageCaptureApp.lastEditedSpecimenCache;
					copyPreviousTaxonRecord();
				}
			});
		}
		return jButtonCopyPrevTaxon;
	}	
	
	
	
	/**
	 * This method initializes jButtonNext
	 * 	
	 * @return javax.swing.JButton	
	 */
	//this is not the right arrow button!
	private JButton getJButtonNext() {
		if (jButtonNext == null) {
			jButtonNext = new JButton();
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/next.png");
			if (iconFile!=null) { 
			   jButtonNext.setIcon(new ImageIcon(iconFile));
			} else { 
				jButtonNext.setText("N");
			}
			jButtonNext.setMnemonic(KeyEvent.VK_N);
			jButtonNext.setEnabled(myControler.isInTable());
			jButtonNext.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//TODO here save the data in memory for "copy prev"
					try {
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
						// try to move to the next specimen in the table.
						if (thisPane.myControler.nextSpecimenInTable()) { 
						   //thisPane.myControler.setSpecimen(thisPane.specimen.getSpecimenId() + 1);
						   thisPane.setVisible(false);
						   thisPane.myControler.displayInEditor();
						   thisPane.invalidate();
						}
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally { 
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
					}
				}
			});
		}
		log.debug("SpecimenDetailsViewPane.getJButtonNext(): 9");
		return jButtonNext;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPrevious() {
		if (jButtonPrevious == null) {
			jButtonPrevious = new JButton();
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/back.png");
			if (iconFile!=null) {
			   jButtonPrevious.setIcon(new ImageIcon(iconFile));
			} else { 
				jButtonPrevious.setText("P");				
			}
			jButtonPrevious.setMnemonic(KeyEvent.VK_P);
			jButtonPrevious.setToolTipText("Move to Previous Specimen");
			jButtonPrevious.setEnabled(myControler.isInTable());
			jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
						// try to move to the previous specimen in the table.
						if (thisPane.myControler.previousSpecimenInTable()) {
						   thisPane.setVisible(false);
						   thisPane.myControler.displayInEditor();
						   thisPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						   thisPane.invalidate();
						}
						try { 
						thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						} 
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						try { 
						thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));;
						} catch (Exception ex) { 
							log.error(ex);
						}
					}
				}
			});
		}
		return jButtonPrevious;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
		}
		return jPanel1;
	}
	
	private void setStateToClean() { 
		state = STATE_CLEAN;
		// if this is a record that is part of a navigatable set, enable the navigation buttons
		if (myControler!=null){
			log.debug("Has controler");
			if (myControler.isInTable()) { 
				log.debug("controler is in table");
				// test to make sure the buttons have been created before trying to enable them.
				if (jButtonNext!=null) { 
					jButtonNext.setEnabled(true);
				}
				if (jButtonPrevious!=null) { 
					jButtonPrevious.setEnabled(true);
				}
			}
		}
	}
	
	private void setStateToDirty() { 
		state = STATE_DIRTY;
		if (jButtonNext!=null) { 
			this.jButtonNext.setEnabled(false);
		}
		if (jButtonPrevious!=null) { 
			this.jButtonPrevious.setEnabled(false);
		}
	}
	
	/** State of the data in the forms as compared to the specimen from which the data was loaded.
	 * 
	 * @return true if the data as displayed in the forms hasn't changed since the data was last loaded from
	 * or saved to the specimen, otherwise false indicating a dirty record.  
	 */
	private boolean isClean()  {
	    boolean result = false;
	    if (state==STATE_CLEAN) { 
	    	result = true;
	    }
	    return result;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldISODate() {
		if (jTextFieldISODate == null) {
			jTextFieldISODate = new JTextField();
			jTextFieldISODate.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "ISODate", jTextFieldISODate));
			jTextFieldISODate.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "ISODate"));
			jTextFieldISODate.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldISODate;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDets() {
		if (jButtonDeterminations == null) {
			jButtonDeterminations = new JButton();
			jButtonDeterminations.setText("Dets.");
			jButtonDeterminations.setMnemonic(KeyEvent.VK_D);
			
			jButtonDeterminations.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DeterminationFrame dets = new DeterminationFrame(specimen);
					dets.setVisible(true);
				}
			});
		}
		return jButtonDeterminations;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField9() {
		if (jTextFieldCitedInPub == null) {
			jTextFieldCitedInPub = new JTextField();
			jTextFieldCitedInPub.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "CitedInPublication", jTextFieldCitedInPub));
			jTextFieldCitedInPub.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "CitedInPublication"));
			jTextFieldCitedInPub.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldCitedInPub;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneNotes() {
		if (jScrollPaneNotes == null) {
			jScrollPaneNotes = new JScrollPane();
			jScrollPaneNotes.setViewportView(getJTextAreaNotes());
			jScrollPaneNotes.setPreferredSize(new Dimension(0, 30));
			jScrollPaneNotes.setMinimumSize(new Dimension(0, 30));			
			//jScrollPaneNotes.add(getJTextAreaNotes()); //allie!!!
		}
		return jScrollPaneNotes;
	}
	
	/*private JTextField getJScrollPaneNotes() {
		if (jScrollPaneNotes == null) {
			jScrollPaneNotes = new JTextField();
			//jScrollPaneNotes.setViewportView(getJTextAreaNotes());
			jScrollPaneNotes.add(getJTextAreaNotes()); //allie!!!
		}
		return jScrollPaneNotes;
	}*/	

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton13() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Date Emerged");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldDateNos.getText().equals("")) { 
						jTextFieldDateNos.setText(jTextFieldDateEmerged.getText());
					} else { 
						jTextFieldDateEmerged.setText(jTextFieldDateNos.getText());
					}
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Date Collected");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldDateNos.getText().equals("")) { 
						jTextFieldDateNos.setText(jTextFieldDateCollected.getText());
					} else { 
						jTextFieldDateCollected.setText(jTextFieldDateNos.getText());
					}
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButtonSpecificLocality	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSpecificLocality() {
		if (jButtonSpecificLocality == null) {
			jButtonSpecificLocality = new JButton();
			jButtonSpecificLocality.setText("Specific Locality");
			jButtonSpecificLocality.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldVerbatimLocality.getText().equals("")) { 
						if (jTextFieldLocality.getText().equals("")) { 
							// If both are blank, set the blank value string.
							jTextFieldLocality.setText("[no specific locality data]");
						}
						jTextFieldVerbatimLocality.setText(jTextFieldLocality.getText());
					} else { 
						jTextFieldLocality.setText(jTextFieldVerbatimLocality.getText());
					}
				}
			});
		}
		return jButtonSpecificLocality;
	}
	
	private JTextField getJLabelMigrationStatus() {
		if (jLabelMigrationStatus==null) { 
			jLabelMigrationStatus = new JTextField(60);
			//jLabelMigrationStatus.setBackground(null);
			//jLabelMigrationStatus.setBorder(null);
			jLabelMigrationStatus.setEditable(false);
			jLabelMigrationStatus.setText("");
		    if (specimen.isStateDone()) { 
		    	String uri = "http://mczbase.mcz.harvard.edu/guid/MCZ:Ent:" + specimen.getCatNum();
		    	jLabelMigrationStatus.setText(uri);
		    }
		}
	    return jLabelMigrationStatus;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JLabel getJTextFieldImgCount() {
		if (jTextFieldImageCount == null) {
			jTextFieldImageCount = new JLabel("Number of Images=  ");
			jTextFieldDateCreated.setForeground(Color.BLACK);
		}
		return jTextFieldImageCount;
	}

	/*private JLabel getLblHigherGeography() {
		if (lblHigherGeography == null) {
			lblHigherGeography = new JLabel("Higher Geography");
		}
		return lblHigherGeography;
	}
	private FilteringGeogJComboBox getComboBoxHighGeog() {
		if (comboBoxHigherGeog == null) {
			MCZbaseGeogAuthRecLifeCycle mls = new MCZbaseGeogAuthRecLifeCycle();
			comboBoxHigherGeog = new FilteringGeogJComboBox();
			comboBoxHigherGeog.setHGModel(new HigherGeographyComboBoxModel(mls.findAll()));
			comboBoxHigherGeog.setEditable(true);
			comboBoxHigherGeog.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log.debug(e.getActionCommand());
					comboBoxHigherGeog.getSelectedIndex();
					log.debug(comboBoxHigherGeog.getSelectedItem());
					log.debug("Selected Index: " + comboBoxHigherGeog.getSelectedIndex());
					MCZbaseGeogAuthRec utl = (MCZbaseGeogAuthRec) ((HigherGeographyComboBoxModel)comboBoxHigherGeog.getModel()).getSelectedItem();
					if (utl==null) { 
						log.debug("null");
					} else { 
					    log.debug(utl.getHigher_geog());
					    specimen.setHigherGeography(utl.getHigher_geog());
					}
				}
			});
		}
		return comboBoxHigherGeog;
	}*/
	private JTextField getTextFieldMaxElev() {
		if (textFieldMaxElev == null) {
			textFieldMaxElev = new JTextField();
			textFieldMaxElev.setColumns(10);
		}
		return textFieldMaxElev;
	}
	private JComboBox getComboBoxElevUnits() {
		if (comboBoxElevUnits == null) {
			comboBoxElevUnits = new JComboBox();
			comboBoxElevUnits.setModel(new DefaultComboBoxModel(new String[] {"", "?", "m", "ft"}));
		}
		return comboBoxElevUnits;
	}
	private JLabel getLblTo() {
		if (lblTo == null) {
			lblTo = new JLabel("to");
		}
		return lblTo;
	}
	private JLabel getLblMicrohabitat() {
		if (lblMicrohabitat == null) {
			lblMicrohabitat = new JLabel("Microhabitat");
		}
		return lblMicrohabitat;
	}
	private JTextField getMicrohabitTextField() {
		if (microhabitTextField == null) {
			microhabitTextField = new JTextField();
			microhabitTextField.setColumns(10);
		}
		log.debug("microhabit text field is " + microhabitTextField);
		return microhabitTextField;
	}
	private JLabel getLblNatureofid() {
		if (lblNatureofid == null) {
			lblNatureofid = new JLabel("NatureOfID");
		}
		return lblNatureofid;
	}
	private JLabel getLblIdDate() {
		if (lblIdDate == null) {
			lblIdDate = new JLabel("ID Date");
		}
		return lblIdDate;
	}
	private JLabel getLblIdRemarks() {
		if (lblIdRemarks == null) {
			lblIdRemarks = new JLabel("Id Remarks");
		}
		return lblIdRemarks;
	}
	
	
	/**
	 * This method initializes jTextFieldDateDetermined
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateDetermined() {
		if (jTextFieldDateDetermined == null) {
			jTextFieldDateDetermined = new JTextField();
			jTextFieldDateDetermined.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "ISODate", jTextFieldDateDetermined));
			jTextFieldDateDetermined.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateIdentified"));
			jTextFieldDateDetermined.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateDetermined;
	}
	
	/**
	 * This method initializes the Determier pick list.
	 * 	
	 * @return FilteringAgentJComboBox
	 */
	//original code
	/*private FilteringAgentJComboBox getJCBDeterminer() {
		if (jCBDeterminer == null) {
			jCBDeterminer = new FilteringAgentJComboBox();
			jCBDeterminer.setEditable(true);
			jCBDeterminer.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "IdentifiedBy"));
			jCBDeterminer.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jCBDeterminer;
	}	*/

	//allie change 1
	/*private JTextField getJCBDeterminer() {
		if (jCBDeterminer == null) {
			jCBDeterminer = new JTextField();
			jCBDeterminer.setEditable(true);
			jCBDeterminer.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "IdentifiedBy"));
			jCBDeterminer.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jCBDeterminer;
	}*/
	//allie change 2	
	private JComboBox getJCBDeterminer() {
		log.debug("calling getJCBDeterminer() ... it is " + jCBDeterminer);
		if (jCBDeterminer == null) {
			log.debug("initt jCBDeterminer determiner null, making a new one");
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jCBDeterminer = new JComboBox<String>();
			jCBDeterminer.setModel(new DefaultComboBoxModel<String>(sls.getDistinctDeterminers()));
			jCBDeterminer.setEditable(true);
			//jComboBoxCollection.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Collection", jComboBoxCollection));
			//jCBDeterminer.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Collection"));
			jCBDeterminer.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jCBDeterminer);
		}
		return jCBDeterminer;
	}
	
	
	/**
	 * This method initializes type status pick list
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox<String> getCbTypeStatus() {
		if (cbTypeStatus == null) {
			cbTypeStatus = new JComboBox<String>(TypeStatus.getTypeStatusValues());
			// cbTypeStatus = new JComboBox(TypeStatus.getTypeStatusValues());  // for visual editor
			cbTypeStatus.setEditable(true);
			cbTypeStatus.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "TypeStatus"));
			cbTypeStatus.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return cbTypeStatus;
	}	
	
	/**
	 * This method initializes jTextFieldIdRemarks
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldIdRemarks() {
		if (jTextFieldIdRemarks == null) {
			jTextFieldIdRemarks = new JTextField();
			jTextFieldIdRemarks.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "IdentificationRemarks"));
			jTextFieldIdRemarks.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldIdRemarks;
	}		
	
	private JLabel getLblTypestatus() {
		if (lblTypestatus == null) {
			lblTypestatus = new JLabel("TypeStatus");
		}
		return lblTypestatus;
	}
}  //  @jve:decl-index=0:visual-constraint="10,15"
