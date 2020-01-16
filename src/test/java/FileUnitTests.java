import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import parser.JsonParser;
import shop.Cart;
import shop.RealItem;
import shop.VirtualItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Tag("JsonParserTests")
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

  @BeforeEach
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

  @Test
  @Disabled("As per task 10.5")
  public void fileIsCreated() {
    jsonParser.writeToFile(testCart);
    Assertions.assertTrue(TEST_CART_FILE.exists());
  }

  @Test
  public void cartIsWrittenToJsonFile() {
    jsonParser.writeToFile(testCart);
    try (BufferedReader reader = new BufferedReader(new FileReader(TEST_CART_FILE))) {
      Cart cartFromFile = gson.fromJson(reader.readLine(), Cart.class);
      assertAll(
          () -> assertEquals(testCart.getCartName(), cartFromFile.getCartName()),
          () -> assertEquals(testCart.getTotalPrice(),cartFromFile.getTotalPrice()));
    } catch (IOException e) {
      e.printStackTrace();
      Assertions.fail("File is not read");
    }
  }

  @Test
  public void cartIsReadFromJsonFile() {
    jsonParser.writeToFile(testCart);
    Cart fromFile = jsonParser.readFromFile(TEST_CART_FILE);
    assertEquals(EXPECTED_TEST_CART_JSON, gson.toJson(fromFile));
  }

  @AfterEach
  public void afterEachTest() {
    TEST_CART_FILE.delete();
  }
}
