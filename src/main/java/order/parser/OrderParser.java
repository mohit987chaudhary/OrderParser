package order.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.bean.CsvToBeanBuilder;

/**
 * @author Mohit
 * 
 */
public class OrderParser {
	private static List<Order> allOrders = new ArrayList<>();
	private static int i = 1;

	public static void main(String[] args) throws Exception {

		for (String file : args) {
			if (isFileExist(file)) {
				String ext = getFileExtension(file);
				if (ext.equalsIgnoreCase("json")) {
					parseOrdersFromJson(file);
				}
				if (ext.equalsIgnoreCase("csv")) {
					parseOrdersFromCsv(file);
				}
			}else {
				System.out.println(file+" not found.");
			}
		}

		if (allOrders.size() > 0) {
			printOrders();
		} else {
			System.out.println("No Record Found.");
		}

	}

	private static boolean isFileExist(String name) {
		File f = new File(name);
		return f.exists();
	}

	private static void parseOrdersFromCsv(String csvFile) throws IllegalStateException, FileNotFoundException {
		List<Order> beans = new CsvToBeanBuilder(new FileReader(csvFile)).withType(Order.class).withSkipLines(1).build()
				.parse();
		i = 1;
		for (Order o : beans) {
			o.setFilename(csvFile);
			o.setLine("" + i++);
		}
		allOrders.addAll(beans);
	}

	private static void parseOrdersFromJson(String jsonFileName) throws IOException, ParseException {
		i = 1;
		JSONParser jsonParser = new JSONParser();
		FileReader jsonFile = new FileReader(jsonFileName);
		Object obj = jsonParser.parse(jsonFile);
		JSONArray orderList = (JSONArray) obj;
		orderList.forEach(order -> parseOrdersObject((JSONObject) order, jsonFileName));
	}

	private static void printOrders() {
		i = 1;
		for (Order o : allOrders) {
			System.out.println("{" + "id: " + i + ", orderId: " + o.getOrderId() + ", amount: " + o.getAmount()
					+ ", comment: " + o.getComment() + ", filename: " + o.getFilename() + ", line: " + o.getLine()
					+ ", result: " + "OK" + "}");
			i++;
		}
	}

	private static void parseOrdersObject(JSONObject orderObject, String jsonFileName) {
		Long orderId = (Long) orderObject.get("orderId");
		Double amount = (Double) orderObject.get("amount");
		String currency = (String) orderObject.get("currency");
		String comment = (String) orderObject.get("comment");

		Order o = new Order();
		o.setOrderId(orderId);
		o.setAmount(amount);
		o.setCurrency(currency);
		o.setComment(comment);
		o.setFilename(jsonFileName);
		o.setLine("" + i++);
		allOrders.add(o);
	}

	public static String getFileExtension(String fullName) {
		String fileName = new File(fullName).getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

}
