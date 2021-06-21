package foo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.server.spi.auth.EspAuthenticator;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

@Api(name = "myApi",
     version = "v1",
     audiences = "53230070017-qglnlh23t1g8u04eqp9hogqpfqe2vj8o.apps.googleusercontent.com",
  	 clientIds = "53230070017-qglnlh23t1g8u04eqp9hogqpfqe2vj8o.apps.googleusercontent.com",
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class PetitionEndpoint {

    @ApiMethod(name = "addPetition", httpMethod=HttpMethod.POST)
    public Entity addPetition(User owner, PostMessage pm) throws UnauthorizedException {
		if (owner == null) {
				throw new UnauthorizedException("Invalid credentials");
			}
		Date date = new Date(); 
        Entity e = new Entity("Petition", Long.MAX_VALUE-(date.getTime()) + "/" + owner.getEmail());
        e.setProperty("titre",pm.titre);
		e.setProperty("body", pm.body);
        e.setProperty("owner", owner.getEmail());
        e.setProperty("date", date);
        e.setProperty("tag", pm.tag);
				
		//cree des signataire
		ArrayList<String> fset = new ArrayList<String>();
		fset.add(owner.getEmail());
		e.setProperty("signatory",fset);
		e.setProperty("nbSignatory",1);
				
				
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
    }

    //Recuperer les petitions signees par l'utilisateur
    @ApiMethod(name = "getMyPetitionsSign", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> getMyPetitionsSign(@Named("owner") User user) throws UnauthorizedException {
		if (user == null) {
				throw new UnauthorizedException("Invalid credentials");
			}
		Query q = new Query("Petition").setFilter(new FilterPredicate("signatory", FilterOperator.EQUAL, user.getEmail()));
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        PreparedQuery pq = datastore.prepare(q);
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(5);

        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);

		return CollectionResponse.<Entity>builder().setItems(results).build();
    }
    
    //Recupere les 100 petitions avec le plus de signatures
    @ApiMethod(name = "getTopPetition", httpMethod = HttpMethod.GET)
	public List<Entity> getTopPetition() {
		Query q = new Query("Petition").addSort("nbSignatory", SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
    }  

    //Recupere les petitions en fonction du tag cherché
    @ApiMethod(name = "getPetitionByTag", httpMethod = HttpMethod.GET)
	public List<Entity> getPetitionByTag(@Named("tag") String tag) {
		Query q = new Query("Petition").setFilter(new FilterPredicate("tag", FilterOperator.EQUAL, tag));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
    }  

    //Signer une petition
    @ApiMethod(name = "signPetition", httpMethod = HttpMethod.POST)
    public Entity signPetition(User user, PostMessage pm ) throws UnauthorizedException {
        if (user == null) {
				throw new UnauthorizedException("Invalid credentials");
            }
        Entity isPetitionSign = new Entity("Petition","true");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key pkey = KeyFactory.createKey("Petition", pm.key);
        Entity ent = new Entity("Petition","hello");
        
        Transaction txn=datastore.beginTransaction();

        try {
				ent = datastore.get(pkey);
				int nb =  Integer.parseInt(ent.getProperty("nbSignatory").toString());
			    ArrayList<String> signatories = (ArrayList<String>) ent.getProperty("signatory");
			    
			    if(!signatories.contains(user.getEmail())) {
			    	signatories.add(user.getEmail());
			    	ent.setProperty("signatory", signatories);
				    ent.setProperty("nbSignatory", nb + 1 );
				    isPetitionSign = new Entity("Petition","false");
				   }
				datastore.put(ent);
				txn.commit();
			} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			  finally {
				if (txn.isActive()) {
				    txn.rollback();
				  }
			}
			return isPetitionSign;  
    }
}