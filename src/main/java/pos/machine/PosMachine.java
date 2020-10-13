package pos.machine;

import java.util.ArrayList;
import java.util.List;

public class PosMachine {

    private static final String UNIT = " (yuan)";
    private static final String NEW_LINE = "\n";
    private static final String DELIMITER = ",";

    public String printReceipt(List<String> barcodes) {
        List<Item> itemsWithDetail = convertToItems(barcodes);
        Receipt receipt = calculateReceipt(itemsWithDetail);
        String receiptOutput = renderReceipt(receipt);

        return receiptOutput;
    }

    private String renderReceipt(Receipt receipt) {
        String itemDetail = spliceItemsDetail(receipt);
        String receiptOutput = spliceReceipt(itemDetail, receipt.getTotalPrice());
        return receiptOutput;
    }

    private String spliceReceipt(String itemDetail, int totalPrice) {
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append(itemDetail);
        receiptBuilder.append("----------------------");
        receiptBuilder.append(NEW_LINE);
        receiptBuilder.append("Total: "+totalPrice+UNIT );
        receiptBuilder.append(NEW_LINE);
        receiptBuilder.append("**********************");
        return receiptBuilder.toString();
    }

    private String spliceItemsDetail(Receipt receipt) {
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("***<store earning no money>Receipt***");
        receiptBuilder.append(NEW_LINE);
        for(Item item : receipt.getItemDetail()){
            receiptBuilder.append("Name: "+item.getName()+DELIMITER);
            receiptBuilder.append(" Quantity: "+item.getQuantity()+DELIMITER);
            receiptBuilder.append(" Unit price: "+item.getUnitPrice()+UNIT+DELIMITER);
            receiptBuilder.append(" Subtotal: "+item.getSubTotal()+UNIT);
            receiptBuilder.append(NEW_LINE);
        }

        return receiptBuilder.toString();
    }

    private Receipt calculateReceipt(List<Item> itemsWithDetail) {
        List<Item> itemsWithSubTotal = calculateItemsSubTotal(itemsWithDetail);
        int totalPrice = calculateTotalPrice(itemsWithSubTotal);
        return new Receipt(itemsWithSubTotal,totalPrice);
    }

    private int calculateTotalPrice(List<Item> itemsWithSubTotal) {
        int totalPrice = 0;
        for(Item itemWithSubTotal : itemsWithSubTotal){
            totalPrice = totalPrice+itemWithSubTotal.getSubTotal();
        }
        return totalPrice;
    }

    private List<Item> calculateItemsSubTotal(List<Item> itemsWithDetail) {
        for(Item item : itemsWithDetail){
            item.setSubTotal(item.getQuantity()*item.getUnitPrice());
        }

        return itemsWithDetail;
    }

    private List<Item> convertToItems(List<String> barcodes) {
        List<Item> itemsWithDetails = new ArrayList<>();
        List<ItemInfo> itemsInfo = loadAllItemsInfo();

        for(ItemInfo itemInfo : itemsInfo){
            Long count = barcodes.stream().filter(barcode ->barcode.equals(itemInfo.getBarcode())).count();
            itemsWithDetails.add(new Item(itemInfo.getName(),count.intValue(),itemInfo.getPrice()));
        }
        return itemsWithDetails;
    }

    private List<ItemInfo> loadAllItemsInfo() {
        return ItemDataLoader.loadAllItemInfos();
    }
}
