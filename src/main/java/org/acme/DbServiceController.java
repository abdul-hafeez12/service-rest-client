package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/employee")
public class DbServiceController {

    @Inject
    MySQLPool client;

    @GET
    public Multi<Employee> list() {
        return Employee.findAll(client);
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getById(@PathParam("id") Long id) {

        return Employee.findById(client, id)
                .onItem().transform(m -> m != null ? Response.ok(m) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @POST
    public Uni<Boolean> add(@QueryParam("FirstName") String FirstName, @QueryParam("LastName") String LastName, @QueryParam("Email") String Email) {
        return Employee.save(client,FirstName,LastName,Email);

    }

    @DELETE
    @Path("/{id}")
    public Uni<Boolean> delete(@PathParam("id") Long id){

        return Employee.delete(client,id);

    }

    @PUT
    @Path("/{EmployeeID}")
    public Uni<Boolean> put(@PathParam("EmployeeID") Long EmployeeID,@QueryParam("FirstName") String FirstName, @QueryParam("LastName") String LastName, @QueryParam("Email") String Email) {

        return Employee.update(client,EmployeeID,FirstName,LastName,Email);

    }
}