// JobsDashboardMain.java --
//
// JobsDashboardMain.java is part of the JobsDashboard plugin.
//
// Copyright (c) 2005-2010 Electric Cloud, Inc.
// All rights reserved.
//

package ecplugins.JobsDashboard.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.electriccloud.commander.client.domain.Job;
import com.electriccloud.commander.client.domain.ObjectType;
import com.electriccloud.commander.client.domain.Order;
import com.electriccloud.commander.client.domain.Procedure;
import com.electriccloud.commander.client.domain.Property;
import com.electriccloud.commander.client.domain.PropertySheet;
import com.electriccloud.commander.client.requests.FindObjectsFilter;
import com.electriccloud.commander.client.requests.FindObjectsRequest;
import com.electriccloud.commander.client.requests.GetProcedureRequest;
import com.electriccloud.commander.client.requests.GetPropertiesRequest;
import com.electriccloud.commander.client.responses.CommanderError;
import com.electriccloud.commander.client.responses.FindObjectsResponse;
import com.electriccloud.commander.client.responses.FindObjectsResponseCallback;
import com.electriccloud.commander.client.responses.ProcedureCallback;
import com.electriccloud.commander.client.responses.PropertySheetCallback;
import com.electriccloud.commander.gwt.client.ComponentBase;
import com.electriccloud.commander.gwt.client.util.CommanderUrlBuilder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/**
* Basic component that is meant to be cloned and then customized to perform a
* real function.
*/
public class JobsDashboardMain
    extends ComponentBase
{

    private static JobsDashboardMainUiBinder uiBinder = GWT.create(
    		JobsDashboardMainUiBinder.class);
    interface JobsDashboardMainUiBinder extends UiBinder<Widget, JobsDashboardMain> {}

    interface Style extends CssResource {
        String evenRow();
        String oddRow();
        String headerRow();
        String vPanel();
        String important();
        String link();
        String h2();
        String topPanel();
        String mouseCursor();
        String projectHeader();
        String historyHeader();
        String lastRunHeader();
        String procedureHeader();
        String descriptionHeader();
        String procedureFont();
        String successRow();
        String errorRow();
        String warningRow();
        String defaultRow();
        String cusorRow();
    }


    @UiField Anchor configureLink;
    @UiField Label refreshLink;
    @UiField FlexTable dashboardPanel;
    @UiField Style style;
    @UiField Label dashboardTitle;
    @UiField Label errorMessage;

    @UiField FlexTable testPanel;
    
    private JobsDashboardMain iThis;
    
    private FindObjectsResponse iResponse;
    
    final int DEFAULT_NUMBER_OF_JOBS=10;
    final String DEFAULT_PROCEDURE_COLUMN_NAME="Job";
    final String DEFAULT_PROJECT_COLUMN_NAME="Project";
    final int EXPAND_IMAGE = 0;
    final int COLLAPSE_IMAGE = 1;
    
    int m_numberOfjobs=15;
    
    //Need to update this static variable to refresh rate set in configuration
   int REFRESH_INTERVAL = 30000;
/*
   protected void setTimer(final int count) {
	   refreshCounter(count);
   }
*/

    //~ Methods ----------------------------------------------------------------

    /**
     * This function is called by SDK infrastructure to initialize the UI parts of
     * this component.
     *
     * @return                 A widget that the infrastructure should place in
     *                         the UI; usually a panel.
     */
    @Override 
    public Widget doInit()
    {

        iThis = this;

        DashboardResources.INSTANCE.css().ensureInjected();
        DashboardResources.RESOURCES.DashboardStyles().ensureInjected();

        // instantiate the main widget, holding the dashboard
        Widget mainLaunch = uiBinder.createAndBindUi(this);
        iResponse = null;
        
        processPropertySheet(); 
        return mainLaunch;
    }
    
    protected void refreshCounter(final int refreshRate) {
        Timer t = new Timer() {
        	int countDown = refreshRate;
        	public void run() {
				refreshLink.setText(("Refreshing in: " + countDown + " seconds"));
				countDown = countDown -1 ;
                if (countDown == 0) {
                 	countDown=refreshRate;
                 	//processPropertySheetOnRefresh();
                 	processPropertySheetOnRefresh();
                }
			}
        };	
        t.scheduleRepeating(1000);
    }
    
    
    Image generateSuccessImageUrl(double successCount,double totalNumberOfJobs)
    {
    	double successPercentage = (successCount/totalNumberOfJobs) * 100;

    	Image img = null;
        if(successPercentage < 5){

            img=new Image(DashboardResources.RESOURCES.successRatio000Icon());
    	}
    	if(successPercentage > 5 && successPercentage <= 15){
            img=new Image(DashboardResources.RESOURCES.successRatio010Icon());
    	}
    	if(successPercentage > 15 && successPercentage <= 22){
            img=new Image(DashboardResources.RESOURCES.successRatio020Icon());
    	}
    	if(successPercentage > 22 && successPercentage <= 28){
            img=new Image(DashboardResources.RESOURCES.successRatio025Icon());
    	}
        if(successPercentage > 28 && successPercentage <= 35){
            img=new Image(DashboardResources.RESOURCES.successRatio030Icon());
        }
    	if(successPercentage > 35 && successPercentage <= 45){
            img=new Image(DashboardResources.RESOURCES.successRatio040Icon());
    	}
    	if(successPercentage > 45 && successPercentage <= 55){
            img=new Image(DashboardResources.RESOURCES.successRatio050Icon());
    	}
    	if(successPercentage > 55 && successPercentage <= 65){
            img=new Image(DashboardResources.RESOURCES.successRatio060Icon());
    	}
    	if(successPercentage > 65 && successPercentage <= 72){
            img=new Image(DashboardResources.RESOURCES.successRatio070Icon());
    	}
        if(successPercentage > 72 && successPercentage <= 78){
            img=new Image(DashboardResources.RESOURCES.successRatio075Icon());
        }
        if(successPercentage > 78 && successPercentage <= 85){
            img=new Image(DashboardResources.RESOURCES.successRatio080Icon());
        }
        if(successPercentage > 85 && successPercentage <= 95){
            img=new Image(DashboardResources.RESOURCES.successRatio090Icon());
        }
    	if(successPercentage > 95){
            img=new Image(DashboardResources.RESOURCES.successRatio100Icon());
    	}
    
    	return img;
    }
	
    public void processDashboardConfig(PropertySheet ps, Label dashboardTitle, Label projectColumnHeader, Label procedureColumnHeader){
        for (Entry<String, Property> entry : ps.getProperties().entrySet())
        {
            Property prop = entry.getValue();
            if (prop.isStringProperty())
            {
                //no-op properties at the root have no meaning
                Property currentProp = entry.getValue();
                String currentValue = currentProp.getValue();
                if(prop.getName()=="history"){
                	m_numberOfjobs=Integer.parseInt(currentValue);
                }
                
                if(prop.getName()=="refresh"){
                	REFRESH_INTERVAL=Integer.parseInt(currentValue);
                	//refreshTimer.scheduleRepeating(REFRESH_INTERVAL*1000); 
                	//setTimer(REFRESH_INTERVAL);
                	refreshCounter(REFRESH_INTERVAL);
                }

                if(prop.getName()=="procedureColumnHeader"){
                    procedureColumnHeader.setText(currentValue);
                }

                if(prop.getName()=="projectColumnHeader"){
                    projectColumnHeader.setText(currentValue);
                }

                if(prop.getName()=="dashboardTitle"){
                    dashboardTitle.setText(currentValue);
                }
            }
        }
        
    }
    
    private Widget addIdField(String id)
    {
        Hidden idField = new Hidden();
        idField.setName("id");
        idField.setValue(id);
        return idField;
    }


    private String createJobUrl(Job currentJob){
        CommanderUrlBuilder urlBuilder = CommanderUrlBuilder.createLinkUrl("jobDetails","jobs",currentJob.getId().toString());
        return urlBuilder.buildString();
    }


    private void imgClickHandler(ClickEvent event, HorizontalPanel hpProjectRoot, Image[] imgArray){
        HTMLTable.Cell selectedCell = dashboardPanel.getCellForEvent(event);
        int selectedRow=selectedCell.getRowIndex();
        boolean bHide=false;
        Widget hId = dashboardPanel.getWidget(selectedRow,0);
        Widget hExpanded = dashboardPanel.getWidget(selectedRow,2);
            if(((Hidden)hExpanded).getValue()=="false"){
                imgArray[EXPAND_IMAGE].setVisible(false);
                imgArray[COLLAPSE_IMAGE].setVisible(true);
                ((Hidden)hExpanded).setValue("true");
                bHide=true;
            }
            else{
                imgArray[EXPAND_IMAGE].setVisible(true);
                imgArray[COLLAPSE_IMAGE].setVisible(false);
                ((Hidden)hExpanded).setValue("false");
                bHide=false;
            }


        hpProjectRoot.setVisible(false);
        hpProjectRoot.setVisible(true);

        //get the name of the product to hide data for
        Hidden idElement=(Hidden)dashboardPanel.getWidget(selectedRow,0);
        String selectedProduct=idElement.getValue();
        int numTests=0;
        double totalSuccess = 0;
        for(int i=1;i<dashboardPanel.getRowCount();i++){
            if(i!=selectedRow){

                hId = dashboardPanel.getWidget(i,0);
                Widget ratio = dashboardPanel.getWidget(i,1);
                if(((Hidden)hId).getValue().contains(selectedProduct)){
                    double currentSuccessRate=0;

                    numTests++;
                    try{
                        currentSuccessRate=Double.parseDouble(((Hidden)ratio).getValue());
                    }
                    catch(Exception e){
                        numTests--;
                    }
                    totalSuccess+=currentSuccessRate;
                    dashboardPanel.getRowFormatter().setVisible(i,bHide);
                }
            }
        }

        if(bHide==false){
            double overallSuccess=totalSuccess/numTests;
            HorizontalPanel hp = new HorizontalPanel();
            Image img = new Image();
            Label lbl = new Label();
            lbl.setText(String.valueOf((int)overallSuccess)+"%");
            img=generateSuccessImageUrl(totalSuccess/100,numTests);
            hp.add(img);
            hp.add(lbl);
            dashboardPanel.setWidget(selectedRow,5,hp);
        }
        else{
            dashboardPanel.clearCell(selectedRow,5);
        }
        dashboardPanel.getRowFormatter().setVisible(selectedRow,false);
        dashboardPanel.getRowFormatter().setVisible(selectedRow,true);

    }

    private void createConfigureLink(){
        HashMap<String,String> paramMap=new HashMap<String,String>();
        String href="/commander/pages/ActiveWorkflowDashboard/configureDashboard_run?redirectTo="+CommanderUrlBuilder.createRedirectUrl().buildString();
        paramMap.put("redirectTo", CommanderUrlBuilder.createRedirectUrl().buildString());
        CommanderUrlBuilder url = CommanderUrlBuilder.createUrl("pages/JobsDashboard/configureDashboard_run"); //URL °íÄ§!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        url.setParameters(paramMap);
        href=url.buildString();
        configureLink.setHref(href);

    }
    
    
    private void populateTableRow(final String projectName,
            final String procedureName,
            final HTML htmlLb,
            final int row2) {
		FindObjectsRequest findObjectsRequest = this.getRequestFactory().createFindObjectsRequest(ObjectType.job);
		findObjectsRequest.addFilter(new FindObjectsFilter.EqualsFilter("projectName", projectName));
		findObjectsRequest.addFilter(new FindObjectsFilter.EqualsFilter("procedureName", procedureName));
		findObjectsRequest.addSort("modifyTime", Order.descending);
		findObjectsRequest.setNumObjects(m_numberOfjobs);
		findObjectsRequest.setCallback(new FindObjectsResponseCallback(){
			@Override 
			public void handleResponse(FindObjectsResponse response) {

				iResponse = response;
				
				String htmlText="";
				
				List<Job> jobs  = response.getJobs();
				
				if(jobs.isEmpty()){
		    		dashboardPanel.getRowFormatter().addStyleName(row2, style.oddRow());
		    		populateJobRow(projectName, procedureName, null, htmlLb);
					return;
				}
				
				//ftJobs.clear();
				int currentRowItem = m_numberOfjobs+3; //number of jobs + first 3 columns for project,procedure, stats
				
				Job lastJob = jobs.get(0);
				
				if("success".equals(lastJob.getOutcome().toString())) {
					dashboardPanel.getRowFormatter().setStyleName(row2, style.successRow());
					populateJobRow(projectName,procedureName, lastJob, htmlLb);
				}else if ("error".equals(lastJob.getOutcome().toString())) {
					dashboardPanel.getRowFormatter().setStyleName(row2, style.errorRow());
					populateJobRow(projectName,procedureName, lastJob, htmlLb);
				}else if ("warning".equals(lastJob.getOutcome().toString())) {
					dashboardPanel.getRowFormatter().setStyleName(row2, style.warningRow());
					populateJobRow(projectName,procedureName, lastJob, htmlLb);
				}else {
					dashboardPanel.getRowFormatter().setStyleName(row2, style.evenRow());
					populateJobRow(projectName,procedureName, lastJob, htmlLb);
				}
				
			};
			
			@Override
			public void handleError(CommanderError error) {
			// TODO Auto-generated method stub
				Window.alert(error.getMessage());
			}
		});
		//Window.alert("testpopuluate3");
		doRequest(findObjectsRequest);
    }
    
    public void processPropertySheet() {
    	createConfigureLink();
        
        m_numberOfjobs=DEFAULT_NUMBER_OF_JOBS;
        final String propertyPath="/myUser/userSettings/myJobsDashboard";
        GetPropertiesRequest req = getRequestFactory().createGetPropertiesRequest();
        req.setPath(propertyPath);

        req.setRecurse(true);

        req.setCallback(new PropertySheetCallback() {

            @Override public void handleError(CommanderError error){
                errorMessage.setText("Could not read data from "+propertyPath+".\n\nPlease configure the dashboard properties.");
                errorMessage.setVisible(true);
            }
            
            @Override public void handleResponse(PropertySheet response)
            {
                String procedureColumnName=DEFAULT_PROCEDURE_COLUMN_NAME;
                String projectColumnName=DEFAULT_PROJECT_COLUMN_NAME;
                Hidden rowId = new Hidden();
                rowId.setName("show");
                rowId.setValue("true");
                

                Label lblProjectHeader = new Label(DEFAULT_PROJECT_COLUMN_NAME);
                lblProjectHeader.setStyleName(style.projectHeader());

                Label lblProcedureHeader = new Label(DEFAULT_PROCEDURE_COLUMN_NAME);
                lblProcedureHeader.setStyleName(style.procedureHeader());
                
                //Label lblLastRunHeader = new Label("Last run details");
                //lblLastRunHeader.setStyleName(style.lastRunHeader());
                
                Label lblDescription = new Label("Description");
                
                //Label lblHistory = new Label("Jobs (Old -> New)");
                //lblHistory.setStyleName(style.historyHeader());
                

                processDashboardConfig(response, dashboardTitle, lblProjectHeader, lblProcedureHeader);

                dashboardPanel.setWidget(0,0,rowId); //stores project name
                dashboardPanel.setWidget(0,1,null);  //used for storing success ratio in rows
                dashboardPanel.setWidget(0,2,null);  //used for expanded/collapsed state
                dashboardPanel.getRowFormatter().addStyleName(0, style.headerRow());
                
                for (Entry<String, Property> entry : response.getProperties().entrySet()) {
                    Property prop = entry.getValue();
                    if (prop.isStringProperty()) {
                        //no-op properties at the root have no meaning
                    }
                    else {
                        //found property sheet at top level, it is a project definition
                        int row = dashboardPanel.getRowCount();

                        final String projectName = entry.getKey();

                        final HorizontalPanel hpProjectRoot = new HorizontalPanel();
                        final Image[] treeCtrlArray = new Image[2];

                        treeCtrlArray[EXPAND_IMAGE]=new Image(DashboardResources.RESOURCES.expandTreeIcon());
                        treeCtrlArray[COLLAPSE_IMAGE]=new Image(DashboardResources.RESOURCES.collapseTreeIcon());
                        treeCtrlArray[EXPAND_IMAGE].addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                imgClickHandler(event, hpProjectRoot,treeCtrlArray);
                            }
                        });
                        treeCtrlArray[COLLAPSE_IMAGE].addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                imgClickHandler(event, hpProjectRoot, treeCtrlArray);
                            }
                        });

                        Label lblRoot = new Label(projectName);
                        hpProjectRoot.add(treeCtrlArray[EXPAND_IMAGE]);
                        hpProjectRoot.add(treeCtrlArray[COLLAPSE_IMAGE]);
                        hpProjectRoot.add(lblRoot);
                        Hidden hExpanded = new Hidden();

                        //default to items expanded so hide expanded image
                        treeCtrlArray[EXPAND_IMAGE].setVisible(false);
                        hExpanded.setName("expanded");
                        hExpanded.setValue("true");
                        
                        dashboardPanel.setWidget(row,0,addIdField(projectName));
                        dashboardPanel.setWidget(row,1,new Hidden());
                        dashboardPanel.setWidget(row,2,hExpanded);
                        dashboardPanel.setWidget(row,3,hpProjectRoot);
                        dashboardPanel.getRowFormatter().addStyleName(row, style.important());
                        
                        //process project details
                        for (Entry<String, Property> projectSheetEntry : prop.getPropertySheet().getProperties().entrySet()) {
                            final int row2 = dashboardPanel.getRowCount();
                            dashboardPanel.setWidget(row2,0,addIdField(projectName+"-"+String.valueOf(row2)));
                            Property currentProp = projectSheetEntry.getValue();
                            final String procedureName = currentProp.getValue();
                            final HTML htmlLb = new HTML();
                            htmlLb.setStyleName(style.procedureFont());
                            
                            dashboardPanel.setWidget(row2,3,htmlLb);
                            
                            dashboardPanel.getRowFormatter().addStyleName(row2, style.defaultRow());
                            
                    		GetProcedureRequest procRequest = getRequestFactory().createGetProcedureRequest();
                            procRequest.setProcedureName(procedureName);
                            procRequest.setProjectName(projectName);
                            procRequest.setCallback(new ProcedureCallback() {
                                @Override
                                public void handleResponse(Procedure response) {
                                    //To change body of implemented methods use File | Settings | File Templates.
                                	populateTableRow(projectName, procedureName, htmlLb, row2);
                                	
                                }

                                @Override
                                public void handleError(CommanderError error) {
                                    //Ignore and populate table row
                                    //Window.alert("Could not get procedure data: "+errorMessage);
                                	populateTableRow(projectName, procedureName, htmlLb, row2);
                                }
                            });

                            doRequest(procRequest);
                            
                        }
                    }
                }
            }
        });

        doRequest(req);
    }
    
    public void processPropertySheetOnRefresh() {

        final String propertyPath="/myUser/userSettings/myJobsDashboard";
        GetPropertiesRequest req = getRequestFactory().createGetPropertiesRequest();
        req.setPath(propertyPath);

        req.setRecurse(true);

        req.setCallback(new PropertySheetCallback() {

            @Override 
            public void handleError(CommanderError error){
                errorMessage.setText("Could not read data from "+propertyPath+".\n\nPlease configure the dashboard properties.");
                errorMessage.setVisible(true);
            }
            
            @Override 
            public void handleResponse(PropertySheet response)
            {

                dashboardPanel.getRowFormatter().addStyleName(0, style.headerRow());
                int row = 1;
                for (Entry<String, Property> entry : response.getProperties().entrySet()) {
                    Property prop = entry.getValue();
                    if (prop.isStringProperty()) {
                        //no-op properties at the root have no meaning
                    }
                    else {
                    	//found property sheet at top level, it is a project definition
                    	
                        final String projectName = entry.getKey();
                        row+=1;
                        for (Entry<String, Property> projectSheetEntry : prop.getPropertySheet().getProperties().entrySet()) {
                        	Property currentProp = projectSheetEntry.getValue();
                            final int row2=row;
                        	final String procedureName = currentProp.getValue();
                        	final HTML htmlLb = (HTML) dashboardPanel.getWidget(row2, 3);
                         

                            GetProcedureRequest procRequest = getRequestFactory().createGetProcedureRequest();
                            procRequest.setProcedureName(procedureName);
                            procRequest.setProjectName(projectName);
                            procRequest.setCallback(new ProcedureCallback() {
                                @Override
                                public void handleResponse(Procedure response) {
                                    //To change body of implemented methods use File | Settings | File Templates.
                                	populateTableRow(projectName, procedureName, htmlLb, row2);
                                }

                                @Override
                                public void handleError(CommanderError error) {
                                    //Ignore and populate table row
                                    //Window.alert("Could not get procedure data: "+errorMessage);
                                	populateTableRow(projectName, procedureName, htmlLb, row2);
                                }
                            });

                            doRequest(procRequest);
                            row+=1;
                        }
                    }
                }
            }
        });

        doRequest(req);
    }
    
    private void populateJobRow(String projectName, final String procedureName, Job lastJob, final HTML htmlLb){
    	String htmlText="";
		
    	if (lastJob != null) { 
    		
    		DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-d'T'HH:mm:ss.SSS'Z'");
    		DateTimeFormat df1 = DateTimeFormat.getFormat("yyyy-MM-d HH:mm:ss");
    		String jobStartDateStr = lastJob.getStart();
    		Date jobStartDate = df.parse(jobStartDateStr);
    		String startDate = df1.format(jobStartDate);
    		final String jobUrl = createJobUrl(lastJob);
    		final String jobName = lastJob.getName();
    		
    		htmlText+="<p style='text-align:left;padding-left:10px;'><b>"+projectName+"</b></p>";
        	htmlText+="<p style='font-size:40px;text-align:center'><b>"+procedureName+"</b></p>";
    		htmlText+="<p style='text-align:right;padding-right:10px;'> Job Name: "+jobName+"</p>";
    		htmlText+="<p style='text-align:right;padding-right:10px;'> Started At: "+startDate+"</p>";
    		htmlLb.setHTML(htmlText);
    		
    		htmlLb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
                	Window.open(jobUrl, null, null);
				}
			});
    		
    		
    		htmlLb.setStyleName(style.cusorRow());
    	}  	else {
			htmlText ="<p style='color:black;text-align:center'><b>"+procedureName+"</b></p>";
			htmlText += "<p style='color:black;text-align:center'><b>No Jobs Found.</b></p>";
			htmlLb.setHTML(htmlText);
    	}
    }
};
