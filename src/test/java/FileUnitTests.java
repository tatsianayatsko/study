import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import parser.JsonParser;
import shop.Cart;
import shop.RealItem;
import shop.VirtualItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Test(groups = "jsonParserTests")
public class FileUnitTests {
  private Cart testCart;
  private static final File TEST_CART_FILE = new File("src/main/resources/test-cart.json");
  private static final String EXPECTED_TEST_CART_JSON = "{\"cartName\":\"test-cart\",\"realItems\":[{\"weight\":1000.0,\"name\":\"Fiat500\","
      + "\"price\":10000.0}]," +
      "\"virtualItems\":[{\"sizeOnDisk\":5000.0,\"name\":\"App\",\"price\":20.0}],\"total\":12024.0}";
  private JsonParser jsonParser = new JsonParser();
  private Gson gson = new Gson();
  private RealItem realItem = new RealItem();
  private VirtualItem virtualItem = new VirtualItem();

  @BeforeMethod
  public void createCartWithItems() {

    realItem.setWeight(1000);
    realItem.setName("Fiat500");
    realItem.setPrice(10000);

    virtualItem.setSizeOnDisk(5000);
    virtualItem.setName("App");
    virtualItem.setPrice(20);

    testCart = new Cart("test-cart");
    testCart.addRealItem(realItem);
    testCart.addVirtualItem(virtualItem);
  }


 // @Disabled("As per task 20.5")
  @Test(enabled = false)
  public void fileIsCreated() {
    jsonParser.writeToFile(testCart);
    Assert.assertTrue(TEST_CART_FILE.exists());
  }

  @Test
  public void cartIsWrittenToJsonFile() {
    jsonParser.writeToFile(testCart);
    try (BufferedReader reader = new BufferedReader(new FileReader(TEST_CART_FILE))) {
      Cart cartFromFile = gson.fromJson(reader.readLine(), Cart.class);
      SoftAssert soft = new SoftAssert();
      soft.assertEquals(testCart.getCartName(), cartFromFile.getCartName(),"Error on 1st validation");
      soft.assertEquals(testCart.getTotalPrice(),cartFromFile.getTotalPrice(),"Error on 2nd validation");
      soft.assertAll();
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail("File is not read");
    }
  }

  @Test
  public void cartIsReadFromJsonFile() {
    jsonParser.writeToFile(testCart);
    Cart fromFile = jsonParser.readFromFile(TEST_CART_FILE);
    Assert.assertEquals(EXPECTED_TEST_CART_JSON, gson.toJson(fromFile));
  }

  @AfterTest
  public void afterEachTest() {
    TEST_CART_FILE.delete();
  }
}
