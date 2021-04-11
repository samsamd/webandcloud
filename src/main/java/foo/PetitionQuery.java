package foo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.appengine.repackaged.com.google.datastore.v1.CompositeFilter;
import com.google.appengine.repackaged.com.google.datastore.v1.Projection;
import com.google.appengine.repackaged.com.google.datastore.v1.PropertyFilter;

@WebServlet(name = "PetQuery", urlPatterns = { "/pquery" })
public class PetitionQuery extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");



		response.getWriter().print("<h2> Great, get the next 10 results now </h2>");

		
        Query q = new Query("Petition");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));

		response.getWriter().print("<li> result:" + result.size() + "<br>");
		Entity last=null;
		for (Entity entity : result) {
			response.getWriter().print("<li>" + entity.getKey()+ "/" + entity.getProperty("owner"));
			last=entity;
		}
		long t2=System.currentTimeMillis();
		response.getWriter().print("<h2> time(Q1) </h2>");		
		response.getWriter().print("q1:"+(t2-t1)+"\n");

		response.getWriter().print("<h2> petition sign by user200 </h2>");

		q = new Query("Petition").setFilter(new FilterPredicate("signatories", FilterOperator.EQUAL, "user200"));
		datastore = DatastoreServiceFactory.getDatastoreService();

		pq = datastore.prepare(q);
		result = pq.asList(FetchOptions.Builder.withLimit(10));

		response.getWriter().print("<li> result:" + result.size() + "<br>");
		last=null;
		for (Entity entity : result) {
			response.getWriter().print("<li>" + entity.getKey());
			last=entity;
		}
		long t3=System.currentTimeMillis();
		response.getWriter().print("<h2> time(Q2) </h2>");		
		response.getWriter().print("q2:"+(t3-t2)+"\n");

		q = new Query("Petition").addSort("nbSignatory", SortDirection.DESCENDING);
		datastore = DatastoreServiceFactory.getDatastoreService();
		pq = datastore.prepare(q);
		result = pq.asList(FetchOptions.Builder.withLimit(10));
		response.getWriter().print("<li> result:" + result.size() + "<br>");
		last=null;
		for (Entity entity : result) {
			response.getWriter().print("<li>" + entity.getKey()+ "/" + entity.getProperty("owner"));
			last=entity;
		}
		long t4=System.currentTimeMillis();
		response.getWriter().print("<h2> time(Q3) </h2>");		
		response.getWriter().print("q3:"+(t4-t3)+"\n");
		
	}
}
