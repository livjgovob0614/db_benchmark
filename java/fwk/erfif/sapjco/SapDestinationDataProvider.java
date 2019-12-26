/**
 * 
 */
package fwk.erfif.sapjco;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

/**
 * DataProvider
 * @author greatjin
 *
 */
public class SapDestinationDataProvider implements DestinationDataProvider {
 
	
	private DestinationDataEventListener eL;      
    
	private HashMap<String, Properties> destinations;
	
	private static SapDestinationDataProvider provider;
	
	private SapDestinationDataProvider(){
			destinations = new HashMap();
	}
	
	//Static method to retrieve instance
	public static SapDestinationDataProvider getInstance(){
		//System.out.println("Getting MyDestinationDataProvider ... ");
		if(provider == null) {
			provider = new SapDestinationDataProvider();
			if(!Environment.isDestinationDataProviderRegistered())
			{
				Environment.registerDestinationDataProvider(provider);
			}
		}
		return provider;
	}
	
    public Properties getDestinationProperties(String destinationName)   
    {   
		if( destinations.containsKey( destinationName ) ){
			return destinations.get( destinationName );
		} else {
			throw new RuntimeException("Destination " + destinationName + " is not available");   
		}
    }   

    public void setDestinationDataEventListener(DestinationDataEventListener eventListener)   
    {   
        this.eL = eventListener;   
    }   

    public boolean supportsEvents()   
    {   
        return true;   
    }   
    
    //implementation that saves the properties in a very secure way
    void changeProperties(String destName, Properties properties)
    {
        synchronized(destinations)
        {
        	destinations.put(destName, properties);
        }
    }
}

