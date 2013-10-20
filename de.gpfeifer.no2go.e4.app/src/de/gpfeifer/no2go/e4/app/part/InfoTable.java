package de.gpfeifer.no2go.e4.app.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.gpfeifer.no2go.core.No2goCalendarEvent;
import de.gpfeifer.no2go.core.No2goUtil;
import de.gpfeifer.no2go.google3.GoogleUtil;
import de.gpfeifer.no2go.synch.No2goSynchListener;

public class InfoTable implements No2goSynchListener {

	public class EventInfoLableProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof EventInfo)) {
				return "";
			}
			EventInfo info = (EventInfo) element;
			String result ="";
			switch (columnIndex) {
			case 0:
				result = info.getTitle();
				break;
			case 1:
				result = info.printStartTime();
				break;
			case 2:
				result = info.printEndTime();
				break;
			case 3:
				result = info.printNumberOfEvents();
				break;
			case 4:
				result = info.printUnchanged();
				break;
			case 5:
				result = info.printInsert();
				break;
			case 6:
				result = info.printUpdate();
				break;
			case 7:
				result = info.printDelete();
				break;
				
			default:
				break;
			}
			return result;
		}
	}

	public class EventInfoContentProvider implements IStructuredContentProvider  {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getElements(Object inputElement) {
			@SuppressWarnings("unchecked")
			Map<String,EventInfo> map = (Map<String, EventInfo>) inputElement;
			List<EventInfo> list = new ArrayList<EventInfo>();
			
			for (EventInfo eventInfo : map.values()) {
				eventInfo.deletePastEvents();
				if (eventInfo.hasEvents()) {
					list.add(eventInfo);
				}
			}
			
			Collections.sort(list, new Comparator<EventInfo>() {

				@Override
				public int compare(EventInfo e1, EventInfo e2) {
					EventDateTime e1Start = e1.getEvents().get(0).getStart();
					EventDateTime e2Start = e2.getEvents().get(0).getStart();
					return (int)(GoogleUtil.getTime(e1Start) - GoogleUtil.getTime(e2Start));
				}
			});
			return list.toArray();
		}

	}

	private TableViewer tableViewer;
	private Map<String,EventInfo> events = new HashMap<String,EventInfo>();

	/**
	 * Create contents of the application window.
	 * @param parent
	 */

	protected Control create(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		container.setLayoutData(gridData);

		//Create the composite
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		//Add TableColumnLayout
		TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createColumn(layout, "Title", "Title if event",10);
		createColumn(layout, "Begin", "Begin Time", 3);
		createColumn(layout, "End", "End Time", 3);
		createColumn(layout, "#", "Number of events", 1);
		createColumn(layout, "=", "Number of unchanged",  1);
		createColumn(layout, "C", "Created", 1);
		createColumn(layout, "U", "Updated", 1);
		createColumn(layout, "D", "Deleted", 1);
		

		
		tableViewer.setLabelProvider(new EventInfoLableProvider());
		tableViewer.setContentProvider(new EventInfoContentProvider());
//		initWorkgroup();
		return container;
	}

	private void createColumn(TableColumnLayout layout, String title, String tooltip, int weigth) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.CENTER);
		TableColumn tblclmnFirst = tableViewerColumn.getColumn();
		tblclmnFirst.setToolTipText(tooltip);
		layout.setColumnData(tblclmnFirst, new ColumnWeightData(weigth, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnFirst.setText(title);
	}

	public void createOld(Composite parent) {
		// Composite tableComposite = new Composite(parent, SWT.NONE);
		// TableColumnLayout tableColumnLayout = new TableColumnLayout();
		// tableComposite.setLayout(tableColumnLayout);

		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new EventInfoContentProvider());
		tableViewer.setLabelProvider(new EventInfoLableProvider());
		
		
		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Last Inserts", "" };
		int[] bounds = { 300, 160 };

		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				No2goCalendarEvent event = (No2goCalendarEvent) element;
				return event.getTitle();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				No2goCalendarEvent event = (No2goCalendarEvent) element;
				Date start = event.getWhenList().get(0).getStartTime();
				return No2goUtil.printDate(start, false);
			}
		});

		// col = createTableViewerColumn(titles[2], bounds[2], 2);
		// col.setLabelProvider(new ColumnLabelProvider() {
		// @Override
		// public String getText(Object element) {
		// return element.toString();
		// }
		// });

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void info(String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(Event event) {
		EventInfo eventInfo = getEventInfo(event);
		eventInfo.insert++;
	}


	@Override
	public void update(Event event) {
		EventInfo eventInfo = getEventInfo(event);
		eventInfo.update++;
		
	}

	@Override
	public void delete(Event event) {
		EventInfo eventInfo = getEventInfo(event);
		eventInfo.delete++;
	}

	@Override
	public void unchanged(Event event) {
		EventInfo eventInfo = getEventInfo(event);
		eventInfo.unchanged++;
	}

	@Override
	public void synchBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void synchEnd() {
		tableViewer.getControl().getDisplay().syncExec(new Runnable() {
			
			@Override
			public void run() {
				tableViewer.setInput(events);				
			}
		});

		
	}

	private String getNotesId(Event event) {
		return GoogleUtil.getNodesId(event);

	}

	private EventInfo getEventInfo(Event event) {
		String id = getNotesId(event);
		EventInfo eventInfo = events.get(id);
		if (eventInfo == null) {
			eventInfo = new EventInfo(id);
			events.put(id, eventInfo);
		}
		eventInfo.addEvent(event);
		return eventInfo;
	}

}
