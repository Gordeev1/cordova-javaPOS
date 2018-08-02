var exec = require('cordova/exec');

var javaPOS = {
	connectByBluetooth: function(address) {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'connectByBluetooth', [address]);
		});
	},
	connectByNetwork: function(address) {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'connectByNetwork', [address]);
		});
	},
	disconnect: function() {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'disconnect');
		});
	},
	isHaveConnectedPrinter: function() {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'isHaveConnectedPrinter');
		});
	},
	closeDayAndGetReport: function() {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'closeDayAndGetReport');
		});
	},
	printReceipt: function(params) {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'printReceipt', [params]);
		});
	},
	printSlip: function(slip) {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'printSlip', [slip]);
		});
	},
	printRawText: function(text) {
		return new Promise(function(resolve, reject) {
			return exec(resolve, reject, 'javaPOS', 'printRawText', [text]);
		});
	}
};

module.exports = javaPOS;
