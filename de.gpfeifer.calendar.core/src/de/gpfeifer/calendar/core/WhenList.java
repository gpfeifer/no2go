package de.gpfeifer.calendar.core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RDate;

import com.google.gdata.data.extensions.When;

public class WhenList extends AbstractList<When> {


	public WhenList(VEvent event) {
		super();
		this.event = event;
	}

	private VEvent event;
	List<When> list = null;

	@Override
	public When get(int index) {
		return getList().get(index);
	}

	private List<When> getList() {
		if (list == null) {
			list = new ArrayList<When>();
			DtStart start = event.getStartDate();
			if (start == null) {
				return list;
			}
			DtEnd end = event.getEndDate();
			When firstWhen = CalendarUtil.createWhen(start, end);
			list.add(firstWhen);
			Date pstart = start.getDate();
			java.util.Date pend = CalendarUtil.createDateWeekOffset(pstart, 100);
			Period period = new Period(new DateTime(pstart), new DateTime(pend));
			PeriodList recSet = event.calculateRecurrenceSet(period);
			for (Object object : recSet) {
				Period p = (Period) object;
				DateTime s = p.getStart();
				DateTime e = p.getEnd();
				// HACK! An allday event has an nonempty recSet containing 
				if (!(p.getStart().getTime() == firstWhen.getStartTime().getValue())) {
					When when = CalendarUtil.createWhen(s, e);
					list.add(when);
				}
			}
		}
		return list;
	}

	@Override
	public int size() {
		return getList().size();
	}

	@Override
	public boolean add(When when) {
		List<When> l = getList();
		l.add(when);
		if (l.size() == 1){
			DtStart startDate = event.getStartDate();
			if (startDate == null) {
				startDate = new DtStart();
				event.getProperties().add(startDate);
			}
			startDate.setDate(CalendarUtil.createICalDateTime(when.getStartTime()));
			DtEnd endDate = event.getEndDate();
			if (endDate == null) {
				endDate = new DtEnd();
				event.getProperties().add(endDate);
			}
			endDate.setDate(CalendarUtil.createICalDateTime(when.getEndTime()));
		} else {
			RDate rdate = (RDate) event.getProperty(Property.RDATE);
			if (rdate == null) {
				rdate = new RDate();
				event.getProperties().add(rdate);
				rdate.getDates().setUtc(true);
			}
			DateList datelist = rdate.getDates();
			datelist.add(CalendarUtil.createICalDateTime(when.getStartTime()));
		}
		return true;
	}

	@Override
	public String toString() {
		return "WhenList [list=" + list + "]";
	}

	@Override
	public When set(int index, When element) {
		return getList().set(index, element);
	}

}
