

package Project.RestAssuredEcommerceProject;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import Files.LoginRequest;
import Files.LoginResponse;
import Files.OrderDetail;
import Files.Orders;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class EcommerceAPITest {

	public static void main(String[] args) {
		
	//Login
		

RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                           .setContentType(ContentType.JSON).build();
		
LoginRequest loginrequest = new LoginRequest();
loginrequest.setUserEmail("postmangaurav@gmail.com");
loginrequest.setUserPassword("Postmangaurav@1234{}");

RequestSpecification reqlogin = given().relaxedHTTPSValidation().log().all().spec(req).body(loginrequest);
LoginResponse response = reqlogin.when().post("api/ecom/auth/login")
                         .then().log().all().extract().response().as(LoginResponse.class);  

		
System.out.println(response.getToken());		
String token = response.getToken();		
System.out.println(response.getUserId());		
String userID = response.getUserId();		
		

//Add Product
	
RequestSpecification addreqbaseproduct = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                              .addHeader("Authorization", token).build();


RequestSpecification addreqproduct = given().log().all().spec(addreqbaseproduct)
                                     .param("productName", "Picture")
                                     .param("productAddedBy", userID)
                                     .param("productCategory", "fashion")
                                     .param("productSubCategory", "shirts")
                                     .param("productPrice", "11500")
                                     .param("productDescription", "Addias Originals")
                                     .param("productFor", "women")
                                     .multiPart("productImage", new File("C://Users//Gaurav//Pictures//Picture.png"));

String addproductresponse =  addreqproduct.when().log().all().post("/api/ecom/product/add-product")
                             .then().log().all().extract().response().asString();
JsonPath js = new JsonPath(addproductresponse);
String product = js.get("productId");


//Create Order

RequestSpecification CreateOrderReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                                      .addHeader("Authorization", token).setContentType(ContentType.JSON).build();


OrderDetail orderdetail = new OrderDetail();
orderdetail.setCountry("India");
orderdetail.setProductOrderedId(product);

List<OrderDetail> orderdetaillist = new ArrayList<OrderDetail>();
orderdetaillist.add(orderdetail);
Orders orders = new Orders();
orders.setOrders(orderdetaillist);

RequestSpecification CreateOrderRequest = given().log().all().spec(CreateOrderReq).body(orders);

String responseAddOrder = CreateOrderRequest.when().post("/api/ecom/order/create-order").then().log().all().extract().response().asString();
System.out.println(responseAddOrder);



//Delete Order

RequestSpecification deleteproductbase = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                                         .addHeader("Authorization", token).setContentType(ContentType.JSON).build(); 

RequestSpecification deleteproductbasereq = given().log().all()
                                            .spec(deleteproductbase).pathParam("productId", product); 

String deleteproductresponse = deleteproductbasereq.when().delete("/api/ecom/product/delete-product/{productId}")
                               .then().log().all().extract().response().asString(); 

JsonPath js1 = new JsonPath(deleteproductresponse);
Assert.assertEquals("Product Deleted Successfully", js1.get("message"));


	}
	}   