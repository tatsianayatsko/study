import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import parser.JsonParser;
import parser.NoSuchFileException;
import shop.Cart;
import shop.RealItem;
import shop.VirtualItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UnitTests {
  private static Cart testCart = new Cart("test-cart");
  private static final File TANYA_CART_FILE = new File("src/test/resources/tanya-cart.json");
  private static final String EXPECTED_TANYA_CART_JSON =
      ("{\"cartName\":\"tanya-cart\",\"realItems\":[{\"weight\":1400.0,\"name\":\"BMW\",\"price\":22103" + ".9}]," + "\"virtualItems" +
          "\":[{\"sizeOnDisk\":8500.0,\"name\":\"Microsoft office\",\"price\":30.0}],\"total\":26560.68}");
  private Gson gson = new Gson();
  private JsonParser jsonParser = new JsonParser();
  private static RealItem realItem = new RealItem();
  private static VirtualItem virtualItem = new VirtualItem();
  private VirtualItem virtualItem2 = new VirtualItem();

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
  }

  @Tag("shopTests")
  @Test
  public void getRealItemWeight() {
    assertEquals(1000, realItem.getWeight());
  }

  @Tag("shopTests")
  @Test
  public void setVirtualItemSizeOnDisk() {
    virtualItem.setSizeOnDisk(6000);
    assertEquals(6000, virtualItem.getSizeOnDisk());
  }

  @Tag("shopTests")
  @Test
  public void getVirtualItemParameters() {
    assertAll(
        () -> assertEquals("App", virtualItem.getName()),
        () -> assertEquals(20, virtualItem.getPrice()),
        () -> assertEquals(5000, virtualItem.getSizeOnDisk()));
  }

  @Tag("shopTests")
  @Test
  public void addItemAndGetTotalPriceTest() {
    virtualItem2.setName("Antivirus");
    virtualItem2.setPrice(10);
    testCart.addVirtualItem(virtualItem2);

    assertEquals(12036, testCart.getTotalPrice());
  }

  @Tag("jsonParserTests")
  @ParameterizedTest
  @ValueSource( strings = {
      "src/test/resources/notExistingFile.json",
      "\\src\\test\\resources\\tanya-cart.json",
      "",
      "test/resources/tanya-cart.json",
      "src/test/resources/"
      })

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
