package at.htl.entity;

public class ToDoItem {
    private Long id;
    private String assignedTo;
    private String description;
    private boolean done;

    //region constructors
    public ToDoItem() {
    }

    public ToDoItem(Long id, String assignedTo, String description, boolean done) {
        this.id = id;
        this.assignedTo = assignedTo;
        this.description = description;
        this.done = done;
    }
    //endregion

    //region getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
    //endregion

}
