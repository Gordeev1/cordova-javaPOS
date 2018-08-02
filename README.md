# Cordova JavaPOS

Cordova JavaPOS is a plugin for [JavaPOS-Shtrih](https://github.com/shtrih-m/javapos_shtrih) driver provides methods to connect and interact with Shtrih-M fiscal printers.

# Installation

```sh
cordova plugin add https://github.com/gordeev1/cordova-javaPOS
```

# Supported Platforms

-   Android

# API

## Methods

-   [javaPOS.connectByBluetooth](#connectByBluetooth)
-   [javaPOS.connectByNetwork](#connectByNetwork)
-   [javaPOS.disconnect](#disconnect)
-   [javaPOS.isHaveConnectedPrinter](#isHaveConnectedPrinter)
-   [javaPOS.printReceipt](#printReceipt)
-   [javaPOS.printSlip](#printSlip)
-   [javaPOS.printRawText](#printRawText)
-   [javaPOS.closeDayAndGetReport](#closeDayAndGetReport)

### connectByBluetooth

```javascript
javaPOS.connectByBluetooth('30:AE:A4:A0:02:5A').then(() => {});
```

### connectByNetwork

```javascript
javaPOS.connectByNetwork('192.168.1.1:3000').then(() => {});
```

### disconnect

```javascript
javaPOS.disconnect().then(() => {});
```

### isHaveConnectedPrinter

```typescript
javaPOS.isHaveConnectedPrinter().then((have: boolean) => console.log(have));
```

### printReceipt

```javascript
const params = {
	paymentSystem: 'VISA',
	customerPhone: '89051901810',
	items: [
		{
			name: 'Планшет Samsung Galaxy Tab A 16GB White',
			price: 2000000, // 20 000 руб 00 коп
			description: 'Планшет Samsung Galaxy Tab A 9.7" SM-T555 16 Gb LTE оснащён 9,7-дюймовым экраном с соотношением сторон 4:3. Он идеально подходит для чтения аудиокниг и онлайн-медиа.'
			count: 2
		},
		{
			name: 'Смартфон Samsung Galaxy 32GB White',
			price: 5800000, // 58 000 руб 00 коп
			count: 4
		}
	]
};

javaPOS.printReceipt(params).then(report => console.log(report));
```

```javascript
{
    docNumber: '21',
    shiftNumber: "11"
}
```

### printSlip

```javascript
const slip = [
	'11.07.18     13:42    ЧЕК   0001',
	'             Оплата             ',
	'Терминал:               00569988',
	'Мерчант:            564444445555',
	'Visa              A0000000031010',
	'Карта:(E1)      ************8943',
	'Клиент:                        /',
	'Сумма (Руб):',
	'          750.00',
	'Комиссия за операцию - 0 Руб.',
	'            ОДОБРЕНО',
	'Код авторизации:          34N202',
	'Номер ссылки:       153130574877',
	' Подпись клиента не требуется  ',
	'57DF7DF4E7A148D1A61C581F1BDB18F0',
	'================================',
	''
];

javaPOS.printSlip(slip).then(() => {});
```

### printRawText

```javascript
javaPOS.printRawText('some raw text').then(() => {});
```

### closeDayAndGetReport

```javascript
javaPOS.closeDayAndGetReport().then(report => console.log(report));
```

```javascript
{
}
```

## TODO

-   close day report
-   javaPOS.printReceipt - currency param
-   javaPOS.printReceipt - VAT param
-   open shift with cashier params
