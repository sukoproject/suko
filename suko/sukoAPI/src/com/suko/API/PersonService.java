package com.suko.API;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.suko.DAO.business.Result;
import com.suko.service.Search;

@Path("/persons")
public class PersonService {

    @GET
    @Path("/{question}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getPerson( @PathParam("question") String question ) {
        return Search.SearchPerson(question, null, null);
    }    
}