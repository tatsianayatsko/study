import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import parser.JsonParser;
import parser.NoSuchFileException;
import shop.Cart;
import shop.RealItem;
import shop.VirtualItem;

import java.io.File;

public class UnitTests {
  private static Cart testCart = new Cart("test-cart");
  private static final File TANYA_CART_FILE = new File("src/test/resources/tanya-cart.json");
  private static final String EXPECTED_TANYA_CART_JSON = ("{\"cartName\":\"tanya-cart\",\"realItems\":[{\"weight\":1400.0," + "\"name\":\"BMW\",\"price\":22103" + ".9}]," + "\"virtualItems" + "\":[{\"sizeOnDisk\":8500.0,\"name\":\"Microsoft office\"," + "\"price\":30.0}],\"total\":26560.68}");
  private Gson gson = new Gson();
  private JsonParser jsonParser = new JsonParser();
  private static RealItem realItem = new RealItem();
  private static VirtualItem virtualItem = new VirtualItem();
  private static VirtualItem virtualItem2 = new VirtualItem();

  @BeforeAll
  public static void setItemsAndAddToCart() {
    realItem.setWeight(1000);
    realItem.setName("Fiat500");
    realItem.setPrice(10000);

    virtualItem.setSizeOnDisk(5000);
    virtualItem.setName("App");
    virtualItem.setPrice(20);

    testCart.addRealItem(realItem);
    testCart.addVirtualItem(virtualItem);

    virtualItem2.setName("Antivirus");
    virtualItem2.setPrice(10);
  }

  @Tag("shopTests")
  @Test
  public void realItemToStringTest() {
   String stringRealItem = realItem.toString();
   assertEquals(String.format("Class: %s; Name: %s; Price: %s; ", realItem.getClass(), realItem.getName(), realItem.getPrice()) + "Weight"
           + ": " + realItem.getWeight(), stringRealItem);
  }

  @Tag("shopTests")
  @Test
  public void virtualItemToStringTest() {
    String stringVirtualItem = virtualItem.toString();
    assertEquals(String.format("Class: %s; Name: %s; Price: %s; ", virtualItem.getClass(), virtualItem.getName(), virtualItem.getPrice()) +
        "Size on disk: " + virtualItem.getSizeOnDisk(), stringVirtualItem);
  }

  @Tag("shopTests")
  @Test
  public void addItemAndGetTotalPriceTest() {
    testCart.addVirtualItem(virtualItem2);
    assertEquals(12036, testCart.getTotalPrice());
  }

  @Tag("shopTests")
  @Test
  public void deleteItemAndGetTotalPriceTest() {
    double expectedTotalPrice = testCart.getTotalPrice();
    testCart.addVirtualItem(virtualItem2);
    testCart.deleteVirtualItem(virtualItem2);
    assertEquals(expectedTotalPrice, testCart.getTotalPrice());
  }

  @Tag("jsonParserTests")
  @ParameterizedTest
  @ValueSource( strings = {
      "src/test/resources/notExistingFile.json",
      "\\src\\test\\resources\\tanya-cart.json",
      "",
      "test/resources/tanya-cart.json",
      "src/test/resources/"})
  public void fileNotFoundExceptionTest(File file) {
    Exception exception = assertThrows(NoSuchFileException.class,
        () -> jsonParser.readFromFile(file));
    assertEquals(exception.getMessage(), (String.format("File %s.json not found!", file)));
  }

  @Tag("jsonParserTests")
  @Test
  public void cartIsReadFromJsonFile() {
    Cart fromFile = jsonParser.readFromFile(TANYA_CART_FILE);
    assertEquals(EXPECTED_TANYA_CART_JSON, gson.toJson(fromFile));
  }
}
