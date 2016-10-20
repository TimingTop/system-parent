//发布，订阅 模式

var pubsub = {
	var q = {};
	var topics = {};
	var subUid = -1;

	//订阅者
	q.subscribe = function (topic, func){
		topics[topic] = topics[topic]? topics[topic] : [];
		var token = (++subUid).toString();

		topics[topic].push({
			token:token,
			func:func
		});

		return token;
	}
	//发布者
	q.publish = function (topic, args) {

		if(!topics[topic]) {
			return;
		}
		var subs = topics[topic];
		var len = subs.length;
		while(len--) {
			subs[len].func(topic, args);
		}
		return this;
	}
}

//测试
var logmsg = function(topic, args) {
	console.log("logging:" + topic + ":" + args);
}
//订阅
var sub = pubsub.subscribe('msgMessage', logmsg);
//发布消息，同时就能触发订阅者的事件
pubsub.publish('msgMessage', 'the first publish');
pubsub.publish('msgMessage', 'the second publish');

