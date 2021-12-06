package org.acme;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class Employee {

    private long EmployeeID;
    private String FirstName;
    private String LastName;
    private String Email;

    public Employee(long employeeID, String firstName, String lastName, String email) {
        EmployeeID = employeeID;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
    }

    public Employee() {
    }

    public long getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(long employeeID) {
        EmployeeID = employeeID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public static Multi<Employee> findAll(MySQLPool  client ){

        return client
                .query("SELECT EmployeeID, FirstName, LastName,Email FROM Employee")
                .execute()
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem()
                .transform(Employee::from);
    }


    public static Uni<Employee> findById(MySQLPool  client , Long id){

        return client.preparedQuery("SELECT EmployeeID, FirstName, LastName,Email FROM Employee where EmployeeID = ?")
                .execute(Tuple.of(id))
                .onItem().transform(i -> i.iterator().hasNext() ? from(i.iterator().next()) : null);
    }

    public static Uni<Boolean> save(MySQLPool  client, String FirstName, String LastName, String Email) {

        return client.preparedQuery("INSERT INTO Employee (FirstName,LastName,Email) VALUES (?,?,?)")
                .execute(Tuple.of(FirstName,LastName,Email))
                .onFailure().invoke(failure -> System.out.println("Failed with " + failure.getMessage()))
                .onCancellation().invoke(() -> System.out.println("Downstream has cancelled the interaction"))
                .onItem().transform(i -> i.rowCount() == 1);
    }

    public static Uni<Boolean> delete(MySQLPool  client,Long id ) {

        return client.preparedQuery("DELETE from Employee  where EmployeeID = ?")
                .execute(Tuple.of(id))
                .onItem().transform(i -> i.rowCount() == 1);
    }

    public static Uni<Boolean> update(MySQLPool  client, Long EmployeeID,String FirstName, String LastName,String Email) {
        return client.preparedQuery("UPDATE Employee SET FirstName = ?,LastName = ?,Email = ? where EmployeeID = ?")
                .execute(Tuple.of(FirstName,LastName,Email,EmployeeID))
                .onItem().transform(i -> i.rowCount() == 1);
    }

    private static Employee from(Row row) {
        return new Employee(row.getLong("EmployeeID"), row.getString("FirstName"),row.getString("LastName"),row.getString("Email"));
    }
}
