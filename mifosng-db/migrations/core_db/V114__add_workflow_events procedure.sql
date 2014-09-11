Drop procedure IF EXISTS workflow_events;

DELIMITER ;;


	CREATE DEFINER=`root`@`%` PROCEDURE `workflow_events`(IN  clientid     INT,
										IN  eventname    varchar(60),
										IN  actionname   varchar(60),
										IN  resourceid   varchar(20),
										OUT strjson      text,
										OUT result       char(5))
	BEGIN
		  DECLARE orderid       int(20) DEFAULT 0;
		  DECLARE paidamt       decimal(20, 2) DEFAULT 0;
		  DECLARE planid        int(5) DEFAULT 0;
		  DECLARE orderaction   varchar(25) DEFAULT 0;
		  DECLARE paymodeid     int(5) DEFAULT 0;
		  DECLARE assignedid     int(5) DEFAULT 0;
		  DECLARE receiptno     varchar(20) DEFAULT 0;
		  DECLARE emailid     varchar(120) DEFAULT 0;
		  DECLARE finish BOOLEAN DEFAULT 0;
		  DECLARE CONTINUE HANDLER FOR NOT FOUND SET finish = TRUE;
	If (eventname='Create Ticket' or eventname='Add Comment' or eventname='Close Ticket') and actionname='Send Email' then
		Select assigned_to into assignedid from  b_ticket_master  WHERE id = resourceid; 
		set emailid='rakesh.adlagatta@gmail.com';
		SET result = 'true';
		SET strjson = Concat("Email_Id:",emailid);
	End if;	  
	If eventname='Create Payment'  and actionname='Renewal' then
		  SELECT amount_paid, paymode_id,receipt_no INTO paidamt, paymodeid,receiptno
			FROM b_payments WHERE id = resourceid;

		  IF paymodeid = 121 THEN
			 IF paidamt = 8000 THEN
				SET planid = 2;
			 ELSEIF paidamt = 16000 THEN
				SET planid = 1;
			 ELSEIF paidamt = 20000 THEN
				SET planid = 3;
			 ELSE
				SET planid = 0;
				SET result = 'false';
				SET finish = TRUE;
			 END IF;

			 IF planid > 0 THEN
				SELECT o.id, ev.enum_value INTO orderid, orderaction
				  FROM b_payments p
					   LEFT JOIN (SELECT max(id) id, client_id FROM b_orders o2 GROUP BY client_id) mo ON (p.client_id = mo.client_id)
					   LEFT JOIN b_orders o ON (o.id = mo.id)
					   LEFT JOIN r_enum_value ev ON (    o.order_status = ev.enum_id AND ev.enum_name = 'order_status')
				 WHERE     p.id = resourceid AND p.client_id = clientid AND o.plan_id = planid;

				IF orderid = 0 OR orderid = NULL THEN
				   SET result = 'true';
				   SET strjson = Concat("action:New"," orderid:0"," planid:",planid);
				   INSERT INTO workflow_eventslogs VALUES (0,clientid,eventname,actionname,resourceid,strjson,result);
				   Update b_paymentgateway set status='Processed' where receipt_no = receiptno;
				ELSE
				   SET result = 'true';
				   SET strjson = Concat("action:",orderaction," orderid:",orderid," planid:",planid);
				   INSERT INTO workflow_eventslogs VALUES (0,clientid,eventname,actionname,resourceid,strjson,result);
				   Update b_paymentgateway set status='Processed' where receipt_no = receiptno;
				END IF;
			 END IF;
		  ELSE
			 SET result = 'false';
			 INSERT INTO workflow_eventslogs VALUES (0,clientid,eventname,actionname,resourceid,strjson,result);
		  END IF;
		  IF finish THEN
			 SET result = 'false';
			 INSERT INTO workflow_eventslogs VALUES (0,clientid,eventname,actionname,resourceid,strjson,result);
		  END IF;

	End if;

	END ;;
DELIMITER ;
