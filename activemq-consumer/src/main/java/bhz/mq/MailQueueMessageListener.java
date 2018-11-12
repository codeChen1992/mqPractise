package bhz.mq;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import bhz.entity.Mail;
import bhz.service.MailService;

import com.alibaba.fastjson.JSONObject;

@Component
public class MailQueueMessageListener implements
		SessionAwareMessageListener<Message> {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination mailQueue;
	@Autowired
	private MailService mailService;

	public synchronized void onMessage(Message message, Session session) {
		try {
			TextMessage msg = (TextMessage) message;
			final String ms = msg.getText();
			System.out.println("收到消息：" + ms);
			// 转换成相应的对象
			Mail mail = JSONObject.parseObject(ms, Mail.class);
			if (mail == null) {
				return;
			}
			try {
				// 执行发送业务
				mailService.mailSend(mail);

			} catch (Exception e) {
				// 发送异常，重新放回队列
				// jmsTemplate.send(mailQueue, new MessageCreator() {
				// @Override
				// public Message createMessage(Session session) throws
				// JMSException {
				// return session.createTextMessage(ms);
				// }
				// });
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
