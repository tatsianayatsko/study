import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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

  @Parameters({"weight","itemName", "price"})
  @BeforeClass
  public static void setItemsAndAddToCart(@Optional("500") double weight,@Optional("Tesla") String itemName,
      @Optional("100000") double price) {
    realItem.setWeight(weight);
    realItem.setName(itemName);
    realItem.setPrice(price);

    virtualItem.setSizeOnDisk(5000);
    virtualItem.setName("App");
    virtualItem.setPrice(20);

    testCart.addRealItem(realItem);
    testCart.addVirtualItem(virtualItem);

    virtualItem2.setName("Antivirus");
    virtualItem2.setPrice(10);
  }

  @Test(groups = "shopTests")
  public void realItemToStringTest() {
   String stringRealItem = realItem.toString();
   Assert.assertEquals(String.format("Class: %s; Name: %s; Price: %s; ", realItem.getClass(), realItem.getName(), realItem.getPrice()) +
       "Weight"
           + ": " + realItem.getWeight(), stringRealItem);
  }

  @Test(groups = "shopTests")
  public void virtualItemToStringTest() {
    String stringVirtualItem = virtualItem.toString();
    Assert.assertEquals(String.format("Class: %s; Name: %s; Price: %s; ", virtualItem.getClass(), virtualItem.getName(),
        virtualItem.getPrice()) +
        "Size on disk: " + virtualItem.getSizeOnDisk(), stringVirtualItem);
  }

  @Parameters("totalPrice")
  @Test(groups = "shopTests")
  public void addItemAndGetTotalPriceTest(@Optional("120036.0") double totalPrice) {
    testCart.addVirtualItem(virtualItem2);
    Assert.assertEquals(totalPrice, testCart.getTotalPrice());
  }

  @Test(groups = "shopTests")
  public void deleteItemAndGetTotalPriceTest() {
    double expectedTotalPrice = testCart.getTotalPrice();
    testCart.addVirtualItem(virtualItem2);
    testCart.deleteVirtualItem(virtualItem2);
    Assert.assertEquals(expectedTotalPrice, testCart.getTotalPrice());
  }

  @DataProvider
  public Object[][] filePath() {
    return new Object[][] {
        {"src/test/resources/notExistingFile.json"},
        {"\\src\\test\\resources\\tanya-cart.json"},
        {"test/resources/tanya-cart.json"},
        {"src/test/resources/"},
        {""},
    };
  }

 @Test(dataProvider = "filePath",
     expectedExceptions = NoSuchFileException.class,
     groups = "jsonParserTests")
 public void fileNotFoundExceptionTest(String path) throws NoSuchFileException{
    jsonParser.readFromFile( new File(path));
      }

  @Test(groups = "jsonParserTests")
  public void cartIsReadFromJsonFile() {
    Cart fromFile = jsonParser.readFromFile(TANYA_CART_FILE);
    Assert.assertEquals(EXPECTED_TANYA_CART_JSON, gson.toJson(fromFile));
  }
}
