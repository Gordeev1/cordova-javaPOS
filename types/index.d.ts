interface Window {
	javaPOS: javaPOS;
}

type DayReport = {};

type ReceiptItem = {
	name: string;
	description?: string;
	price: number;
	count: number;
};

type ReceiptParams = {
	paymentSystem: string;
	customerPhone?: string;
	items: ReceiptItem[];
};

type ReceiptReport = {
	docNumber: string;
	shiftNumber: string;
};

/**
 * The javaPOS object provides methods to connect and manage shtrih-m devices.
 */
interface javaPOS {
	connectByBluetooth: (address: string) => Promise<void>;
	connectByNetwork: (address: string) => Promise<void>;
	disconnect: () => Promise<void>;
	isHaveConnectedPrinter: () => Promise<boolean>;
	closeDayAndGetReport: () => Promise<DayReport>;
	printReceipt: (params: ReceiptParams) => Promise<ReceiptReport>;
	printSlip: (slip: string[]) => Promise<void>;
	printRawText: (slip: string) => Promise<void>;
}

declare var javaPOS: javaPOS;
