import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matcher;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;

import static io.restassured.RestAssured.*;
import java.util.List;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {
        given()  // preparation (token, request body, parameters, cookies...)

                .when() // for url and request method (get, post, put, delete)

                .then(); // response body, assertions, extract data from the response

    }

    @Test
    public void statusCodeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body() // prints the response body
                .log().status() // prints the status
                .statusCode(200); // checks if the status code is 200
    }

    @Test
    public void contentTypeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .contentType(ContentType.JSON); // checks if the response is in JSON format

    }

    @Test
    public void checkCountryFromResponseBody() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country", equalTo("United States"));
    }

    // pm                                               // Rest Assured
    // pm.response.json().'post code'                   // body("'post code'", ...)
    // pm.response.json().places[0].'place name'        // body("places[0].'place name'", ...)
    // body("places.'place name'") gives a list of place names in the places list

    @Test
    public void checkStateFromResponse() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California")); // checks if the state is California
    }

    @Test
    public void bodyArrayHasItem() {
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                //.log().body()
                .statusCode(200)
                .body("places.'place name'", hasItem("Büyükdikili Köyü")); // checks if the list of place names has this value
    }

    @Test
    public void bodyArraySizeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1));
    }

    @Test
    public void bodyArraySizeTest2() {
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .statusCode(200)
                .body("places.'place name'", hasSize(71)); //  checks if the size of the list of place names is 71
    }

    @Test
    public void multipleTests() {

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(71))
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("places[2].'place name'", equalTo("Dörtağaç Köyü"));
    }

    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", "90210")
                .log().uri() // prints the request url
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void pathParamTest1() {
        // send get request for zipcodes between 90210 and 90213 and verify that in all responses the size
        // of the places array is 1

        for (int i = 90210; i <= 90213; i++) {

            given()
                    .pathParam("Country", "us")
                    .pathParam("ZipCode", i)
                    .log().uri() // prints the request url
                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("places", hasSize(1));
        }
    }

    @Test
    public void queryParamTest() {

        given()
                .param("page", 2) // https://gorest.co.in/public/v1/users?page=2
                .when()
                .get("https://gorest.co.in/public/v1/users")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(2));
    }

    @Test
    public void queryParamTest1() {
        // send the same request for the pages between 1-10 and check if
        // the page number we send from request and page number we get from response are the same
        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i) // https://gorest.co.in/public/v1/users?page=2
                    .when()
                    .get("https://gorest.co.in/public/v1/users")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void setup() {
        baseURI = "https://gorest.co.in/public/v1"; // if the request url in the request method doesn't have http part
        // rest assured adds baseURI in front of it
        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)                     // prints request body
                .setContentType(ContentType.JSON)       // sets the data format as JSON
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)  // checks if the status code is 200 from all responses
                .expectContentType(ContentType.JSON)    // checks if the response type is in JSON format
                .log(LogDetail.BODY)                    // prints the body of all responses
                .build();


    }

    @Test
    public void baseURITest() {
        given()
                .param("page", 2)
                .when()
                .get("/users")  //
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(2));
    }

    @Test
    public void requestResponseSpecsTest() {

        given()
                .param("page", 2)
                .spec(requestSpec)
                .when()
                .get("/users")  //
                .then()

                .body("meta.pagination.page", equalTo(2))
                .spec(responseSpec);
    }

    // JSON data extract
    @Test
    public void extractData() {

        String placeName = given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", "90210")
                .log().uri() // prints the request url
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'"); // with extract method all request now returns a value.
        // We can assign it to a variable like String, int, Array ...

        System.out.println(placeName);
    }

    @Test
    public void extractData1() {
        int limit = given()
                .param("page", 2)

                .when()
                .get("/users")  //
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("meta.pagination.limit");

        System.out.println("limit " + limit);
        Assert.assertEquals(limit, 10, "Test is failed");
    }

    @Test
    public void extractData2() {
        // get all ids from the response and verify that 1060492 is among them separately from the request

        List<Integer> listOfIds = given()
                .param("page", 2)

                .when()
                .get("/users")  //
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.id");

        System.out.println(listOfIds.get(1));
        //  Assert.assertTrue(listOfIds.contains(1060492));
        // Assert.assertTrue(listOfIds.contains(1060492));
    }

    @Test
    public void extractData3() {
        // send get request to https://gorest.co.in/public/v1/users.
        // extract all names from data to a list

        List<String> namesList = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.name");

        System.out.println(namesList.get(5));
     //   Assert.assertEquals(namesList.get(5), "Ranjit Devar");
    }

    @Test
    public void extractData4() {

        Response response = given()
                .when()
                .get("/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();

        List<Integer> listOfIds = response.path("data.id");
        List<String> listOfNames = response.path("data.name");
        int limit = response.path("meta.pagination.limit");
        String currentLink = response.path("meta.pagination.links.current");

        System.out.println("listOfIds = " + listOfIds);
        System.out.println("listOfNames = " + listOfNames);
        System.out.println("limit = " + limit);
        System.out.println("currentLink = " + currentLink);

        Assert.assertTrue(listOfNames.contains("Rev. Bhadraksh Gill"));
        Assert.assertTrue(listOfIds.contains(1078203));
        Assert.assertEquals(limit, 10);

    }
    @Test
    public void extractJsonPOJO() {
        // Location                                     // PLace
        // String post code;                            String place name;
        // String country;                              String longitude;
        // String country abbreviation;                 String state;
        // List<Place> places;                          String state abbreviation;
        //String latitude;

        POJO.Location location = given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .extract().as(Location.class);
        System.out.println("location.getCountry()="+location.getCountry());
        System.out.println("location.getPastCode()= "+ location.getPostCode());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = "+ location.getPlaces().get(0).getState());
    }
}
