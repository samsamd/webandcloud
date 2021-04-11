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
     audiences = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
  	 clientIds = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class PetitionEndpoint {

    @ApiMethod(name = "addPetition", httpMethod=HttpMethod.POST)
    public Entity addPetition(String owner, PostMessage pm) throws UnauthorizedException {
        if (owner == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
		
		Date date = new Date(); 
		Entity e = new Entity("Petition", "petition" + "/" + owner);
		e.setProperty("body", pm.body);
		e.setProperty("owner", owner);
		e.setProperty("date", date);
				
		//cree des signataire
		ArrayList<String> fset = new ArrayList<String>();
		fset.add(owner);
		e.setProperty("signatory",fset);
		e.setProperty("nbSignatory",305);
				
		//creer des tags
		HashSet<String> fset2 = new HashSet<String>();
		e.setProperty("tag", fset2);
				
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
    }

    //Recuperer les petitions signes par l'utilisateur
    @ApiMethod(name = "getMyPetition", httpMethod = HttpMethod.GET)
	public List<Entity> getMyPetition(@Named("owner") String owner) throws UnauthorizedException {
		if (owner == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
		Query q = new Query("Petition").setFilter(new FilterPredicate("signatory", FilterOperator.EQUAL, owner;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
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
    
    //Signer une petition
		@ApiMethod(name = "signPetition", httpMethod = HttpMethod.POST)
		public Entity signPetition(String user) throws UnauthorizedException {
			if (user == null) {
				throw new UnauthorizedException("Invalid credentials");
			}
			 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			///utiliser coutingSH pour implémenter


			Key lkey = KeyFactory.createKey("Petition", pm.body);
			Entity ent = new Entity("Petition","hello");
			try {
				ent = datastore.get(lkey);
				int nb =  Integer.parseInt(ent.getProperty("nbSignatory").toString());
			    ent.setProperty("nbSignatory", nb + 1 );
				datastore.put(ent);
				return ent;
			} catch (EntityNotFoundException e) {
			// This should never happen
			}
			return ent;
		}
}