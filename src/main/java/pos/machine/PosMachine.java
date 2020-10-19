package pos.machine;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.frequency;

public class PosMachine {

    private static final String UNIT = " (yuan)";
    private static final String NEW_LINE = "\n";
    private static final int DEFAULT_TOTAL_PRICE = 0;

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
        receiptBuilder.append(String.format("Total: %d%s",totalPrice,UNIT ));
        receiptBuilder.append(NEW_LINE);
        receiptBuilder.append("**********************");
        return receiptBuilder.toString();
    }

    private String spliceItemsDetail(Receipt receipt) {
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("***<store earning no money>Receipt***");
        receiptBuilder.append(NEW_LINE);
        for(Item item : receipt.getItemDetail()){
            receiptBuilder.append(String.format("Name: %s, Quantity: %d, Unit price: %d%s, Subtotal: %d%s",
                    item.getName(),item.getQuantity(),item.getUnitPrice(),UNIT,item.getSubTotal(),UNIT));
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
        int totalPrice = DEFAULT_TOTAL_PRICE;
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
        List<ItemInfo> itemsInfo = loadAllItemsInfo();
        return mapItemInfoWithQuanitity(barcodes,itemsInfo);

    }

    private List<ItemInfo> loadAllItemsInfo() {
        return ItemDataLoader.loadAllItemInfos();
    }

    private List<Item> mapItemInfoWithQuanitity(List<String> barcodes,List<ItemInfo> itemsInfo) {
        List<Item> itemsWithDetails = new ArrayList<>();
        barcodes.stream().distinct().forEach(barcode -> {
            int quantity = frequency(barcodes, barcode);
            ItemInfo itemInfo = getItemInfo(barcode,itemsInfo);
            itemsWithDetails.add(new Item(itemInfo.getName(),quantity,itemInfo.getPrice()));
        });
        return  itemsWithDetails;
    }

    private ItemInfo getItemInfo(String barcode, List<ItemInfo> itemsInfo) {
        return itemsInfo.stream().filter(
                itemInfo -> itemInfo.getBarcode().equals(barcode)).findFirst().orElse(null);
    }
}

