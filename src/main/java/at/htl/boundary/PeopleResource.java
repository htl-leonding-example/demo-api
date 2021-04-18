package at.htl.boundary;

import at.htl.entity.Person;
import at.htl.entity.ToDoItem;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

@Path("/api")
public class PeopleResource {

    List<Person> persons = new LinkedList<>();
    List<ToDoItem> todos = new LinkedList<>();
    long lastId = 0L;

    public PeopleResource() {
    }

    @PostConstruct
    void init() {
        persons.add(new Person("Adam"));
        persons.add(new Person("Eve"));
        persons.add(new Person("Steve"));
    }

    /**
     * https://javaee.github.io/jsonp/
     *
     * @return json array of persons
     */
    @Path("people-json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray hello() {
        //String[] persons = new String[] {"Adam", "Eve", "Snake"};

        return Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("name", "Adam"))
                .add(Json.createObjectBuilder().add("name", "Eve"))
                .add(Json.createObjectBuilder().add("name", "Steve"))
                .build();
    }

    @Path("people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> findAllPersons() {
        return persons;
    }

    @Path("todos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToDoItem> findAllToDoItems() {
        return todos;
    }

    @Path("todos/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToDoItemById(@PathParam("id") Long id) {
        if (todos.isEmpty()) {
            return Response.status(404).build(); // NOT FOUND
        }

        ToDoItem toDoItem = todos.stream()
                .filter(todo -> id.equals(todo.getId()))
                .findAny()
                .orElse(null);

        return Response.ok(toDoItem).build();
    }

    @Path("todos")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createToDoItem(@Context UriInfo info, ToDoItem toDoItem) {
        if (toDoItem.getDescription() == null
                || toDoItem.getDescription().isEmpty()
                || toDoItem.getDescription().isBlank()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{description: 'Missing description'}")
                    .build();
        }

        // Check if assigned-to person exists
        if (toDoItem.getAssignedTo() != null) {
            Person p = persons.stream()
                    .filter(person -> toDoItem.getAssignedTo().equals(person.getName()))
                    .findAny()
                    .orElse(null);
            if (p == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("{description: 'Unknown person'}")
                        .build();

            }
        }


        todos.add(toDoItem);
        toDoItem.setId(++lastId);

        URI location = info
                .getAbsolutePathBuilder()
                .path(Long.toString(toDoItem.getId()))
                .build();
        return Response
                .created(location)
                .entity(toDoItem)
                .build();
    }

    @Path("todos/{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchToDoItem(@PathParam("id") Long id, JsonObject toDoItemJson) {

        // Check, if toDoItem exists
        ToDoItem currentToDoItem = findToDoItemById(id);
        if (currentToDoItem == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        // Update description if specified
        if (toDoItemJson.containsKey("description")) {
            currentToDoItem.setDescription(toDoItemJson.getString("description"));
        }

        // Update done if specified
        if (toDoItemJson.containsKey("done")) {
            currentToDoItem.setDone(toDoItemJson.getBoolean("done"));
        }

        // Update assigned-to if specified
        if (toDoItemJson.containsKey("assignedTo")) {
            Person p = findPersonByName(toDoItemJson.getString("assignedTo"));
            if (p == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("{description: 'Unknown person'}")
                        .build();
            } else {
                currentToDoItem.setAssignedTo(toDoItemJson.getString("assignedTo"));
            }
        }

        return Response
                .ok(currentToDoItem)
                .build();
    }


    @Path("todos/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@PathParam("id") Long id) {

        ToDoItem toDoItem = findToDoItemById(id);

        if (toDoItem == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        todos.remove(toDoItem);

        return Response
                .noContent()
                .build();
    }


    ToDoItem findToDoItemById(long id) {
        return todos.stream()
                .filter(todo -> id == todo.getId())
                .findAny()
                .orElse(null);
    }

    Person findPersonByName(String name) {
        return persons.stream()
                .filter(person -> name.equals(person.getName()))
                .findAny()
                .orElse(null);
    }
}