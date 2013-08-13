package de.gpfeifer.no2go.synch;


public interface No2goSynch {

	void synch() throws Exception;
	
	void addListener(No2goSynchListener listener);
	
	void removeListener(No2goSynchListener listener);


	int delete() throws Exception;
	
}
