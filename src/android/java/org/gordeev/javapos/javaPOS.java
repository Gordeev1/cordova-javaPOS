package org.gordeev.javapos;

import android.util.Log;
import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.*;
import org.json.JSONObject;

import com.shtrih.fiscalprinter.ShtrihFiscalPrinter;
import com.shtrih.fiscalprinter.SmFiscalPrinterException;
import com.shtrih.fiscalprinter.command.FSDocType;
import com.shtrih.fiscalprinter.command.FSStatusInfo;
import com.shtrih.fiscalprinter.command.LongPrinterStatus;
import com.shtrih.jpos.fiscalprinter.JposExceptionHandler;
import com.shtrih.jpos.fiscalprinter.SmFptrConst;

import jpos.FiscalPrinter;
import jpos.JposConst;
import jpos.JposException;

enum PaymentSystem {
    VISA,
    MASTERCARD
}

class ReceiptItem extends  JSONObject {
    String name;
    Optional<String> description;
    int price;
    int count;
};

class ReceiptParams extends JSONObject {
    PaymentSystem paymentSystem;
    Optional<String> customerPhone;
    JSONArray items;
};

public class javaPOS extends CordovaPlugin {

    private static final String TAG = "javaPOS";
    private ShtrihFiscalPrinter printer;

    /**
     * Actions
     */
    private static final String CONNECT_BY_BLUETOOTH = "connectByBluetooth";
    private static final String CONNECT_BY_NETWORK = "connectByNetwork";
    private static final String DISCONNECT = "disconnect";
    private static final String CLOSE_DAY = "closeDayAndGetReport";
    private static final String IS_HAVE_CONNECTED_PRINTER = "isHaveConnectedPrinter";
    private static final String PRINT_RECEIPT = "printReceipt";
    private static final String PRINT_RAW_TEXT = "printRawText";
    private static final String PRINT_SLIP = "printSlip";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d(TAG, "Initializing javaPOS plugin");
        printer = new ShtrihFiscalPrinter(new FiscalPrinter());
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, action);

        if (action.equals(CONNECT_BY_BLUETOOTH)) {
            String address = args.optString(0);
            if (address.isEmpty()) {
                address = "127.0.0.1:12345";
            }
            connectByBluetooth(address, callbackContext);
        }
        if (action.equals(CONNECT_BY_NETWORK)) {
            String address = args.getString(0);
            connectByNetwork(address, callbackContext);
        }
        if (action.equals(DISCONNECT)) {
            disconnect(callbackContext);
        }
        if (action.equals(IS_HAVE_CONNECTED_PRINTER)) {
           isHaveConnectedPrinter(callbackContext);
        }

        if (action.equals(CLOSE_DAY)) {
            closeDay(callbackContext);
        }

        if (action.equals(PRINT_RECEIPT)) {
            JSONObject params = args.getJSONObject(0);
            printReceipt(params, callbackContext);
        }

        if (action.equals(PRINT_RAW_TEXT)) {
            String text = args.getString(0);
            printRawText(text, callbackContext);
        }

        if (action.equals(PRINT_SLIP)) {
            JSONArray slip = args.getJSONArray(0);
            printSlip(slip, callbackContext);
        }

        return true;
    }

    public void connectByBluetooth(String address, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    HashMap<String, String> props = new HashMap<>();
                    props.put("portName", address);
                    props.put("portType", "3");
                    props.put("portClass", "com.shtrih.fiscalprinter.port.BluetoothPort");
                    props.put("protocolType", "0"); // 1.0

                    JposConfig.configure("ShtrihFptr", cordova.getActivity().getApplicationContext(), props);
                    if (printer.getState() != JposConst.JPOS_S_CLOSED) {
                        printer.close();
                    }
                    printer.open("ShtrihFptr");
                    printer.claim(3000);
                    printer.setDeviceEnabled(true);
                    callbackContext.success();

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }

    public void connectByNetwork(String address, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> props = new HashMap<>();
                    props.put("portName", address);
                    props.put("portType", "2");
                    props.put("protocolType", "1");
                    props.put("fastConnect", "1");

                    JposConfig.configure("ShtrihFptr", cordova.getActivity().getApplicationContext(), props);
                    printer.open("ShtrihFptr");
                    printer.claim(3000);
                    printer.setDeviceEnabled(true);
                    callbackContext.success();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    callbackContext.error(e.toString());
                }
            }
        });
    }

    public void disconnect(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    if (printer.getState() != JposConst.JPOS_S_CLOSED) {
                        printer.close();
                    }
                    callbackContext.success();

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }

    public void isHaveConnectedPrinter(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Boolean enabled = printer != null && printer.getDeviceEnabled();
                    PluginResult result = new PluginResult(PluginResult.Status.OK, enabled);
                    callbackContext.success();
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }

            }
        });
    }

    public void printRawText(String slip, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    prepare(printer);
                    printer.printText(slip);
                    callbackContext.success();

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }

    public void printSlip(JSONArray slip, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    prepare(printer);

                    for (int i = 0; i < slip.length(); ++i) {
                        printer.printText(slip.optString(i));
                    }

                    callbackContext.success();

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }


    public void printReceipt(JSONObject params, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    openDayIfNeeded(printer);
                    prepare(printer);

                    printer.setFiscalReceiptType(SmFptrConst.SMFPTR_RT_SALE);
                    printer.beginFiscalReceipt(false);

                    writeTagIfNotNullAndNotEmpty(printer, 1008, params.optString("customerPhone"));

                    int payment = 0;

                    for (int i = 0; i < params.getJSONArray("items").length(); ++i) {
                        try {
                            JSONObject item = params.getJSONArray("items").getJSONObject(i);
                            String name = item.getString("name");
                            String description = item.optString("description");
                            int price = item.getInt("price");
                            int count = item.getInt("count");
                            int totalPrice = price * count;

                            printer.printRecItem(name, totalPrice, count, 1 /* 18% */, price, "rub");
                            if (description != null && !description.isEmpty()) {
                                printer.printRecMessage(description);
                            }

                            payment += totalPrice;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    printer.printRecTotal(payment, payment, params.optString("paymentSystem").equals("VISA") ? "2" : "3");
                    printer.endFiscalReceipt(false);

                    HashMap<String, String> report = new HashMap<>();
                    report.put("docNumber", printer.fsReadStatus().getDocNumber() + "");
                    report.put("shiftNumber", printer.readLongPrinterStatus().getCurrentShiftNumber() + "");

                    PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(report));
                    callbackContext.sendPluginResult(result);

                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }

    public void closeDay(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    openDayIfNeeded(printer);
                    LongPrinterStatus status = printer.readLongPrinterStatus();

                    if (!status.getPrinterMode().isDayClosed()) {
                        printer.fsStartDayClose();
                        printer.printZReport();
                    }

                    HashMap<String, String> report = getDayReport(printer);
                    callbackContext.success(new JSONObject(report));
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    callbackContext.error(message);
                }
            }
        });
    }

    private HashMap<String, String> getDayReport(ShtrihFiscalPrinter printer) throws JposException {
        // TODO: get report
        return new HashMap();
    }

    public void openDayIfNeeded(ShtrihFiscalPrinter printer) throws JposException {
        LongPrinterStatus status = printer.readLongPrinterStatus();

        Log.d(TAG, "day closed - " + status.getPrinterMode().isDayClosed());

        if (status.getPrinterMode().isDayClosed()) {
            printer.fsStartDayOpen();
            printer.openFiscalDay();
        }
    }

    private void prepare(ShtrihFiscalPrinter printer) throws JposException {
        LongPrinterStatus status = printer.readLongPrinterStatus();

        // проверяем наличие бумаги
        if (status.getSubmode() == 1 || status.getSubmode() == 2) {
            final int errorCodeNoPaper = 107;
            throw JposExceptionHandler.getJposException(
                    new SmFiscalPrinterException(errorCodeNoPaper, "Отсутствует бумага")
            );
        }

        // проверяем, есть ли открытый документ в ФН
        FSStatusInfo fsStatus = printer.fsReadStatus();

        if (fsStatus.getDocType().getValue() != FSDocType.FS_DOCTYPE_NONE) {
            printer.fsCancelDocument(); // если есть отменяем
        }

        printer.resetPrinter();
    }

    private void writeTagIfNotNullAndNotEmpty(ShtrihFiscalPrinter printer, int tagId, String value) throws Exception {
        if (value != null && !value.isEmpty())
            printer.fsWriteTag(tagId, value);
    }

}