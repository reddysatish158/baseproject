Drop procedure IF EXISTS codeDescription;
DELIMITER //
create procedure codeDescription() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'code_description'
     and TABLE_NAME = 'm_code'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table m_code add `code_description` varchar(1000) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call codeDescription();

Drop procedure IF EXISTS codeDescription;
Update m_code set code_description='Client Identites list can be added' where id=1;
Update m_code set code_description='Depricated' where id=2;
Update m_code set code_description='Depricated' where id=3;
Update m_code set code_description='Depricated' where id=4;
Update m_code set code_description='Combo values for Yes and No' where id=5;
Update m_code set code_description='Depricated' where id=6;
Update m_code set code_description='Define the Ticket Problem Codes' where id=7;
Update m_code set code_description='Statues of Ticket by default system defined are Open,Working,Overdue,Closed' where id=8;
Update m_code set code_description='The Media is asses while difning set the category of it' where id=9;
Update m_code set code_description='Client Category like Normal, Residential Captured while create/edit client ' where id=10;
Update m_code set code_description='Payment method like cash, cheque etc' where id=11;
Update m_code set code_description='While passing adjustment need to select it Standard Adjustment etc' where id=12;
Update m_code set code_description='Define the Priovisioning system like CAS / BRAS with is_system_defined flag as 0 for renewal before expiry request generated etc' where id=13;
Update m_code set code_description='Media Attributes like Actor Director in media details' where id=14;
Update m_code set code_description='Format secification for Media like SD Standard Definition,HD High Definition used as price variant' where id=15;
Update m_code set code_description='Used in the Dynamic table form combo values' where id=16;
Update m_code set code_description='Defining the charge type Fixed as of now RC,NRC and UC' where id=17;
Update m_code set code_description='While the Order is disconnected Master values of Disconnection Reasons like wrong plan , auto expiry' where id=18;
Update m_code set code_description='Need to know more details and update' where id=19;
Update m_code set code_description='Bill Frequency to set the price and while the order book in general monthly,quaterly' where id=20;
Update m_code set code_description='Need to know more deatails and update' where id=21;
Update m_code set code_description='Laguage combo option for the Media Assets like English, French' where id=22;
Update m_code set code_description='Job Process but need more clarity' where id=23;
Update m_code set code_description='Scheduler Job Type either Recurrin or One Time' where id=24;
Update m_code set code_description='Prospect or Lead call status so that action can be taken' where id=25;
Update m_code set code_description='Prospect Status remark if it is interested or not' where id=26;
Update m_code set code_description='Prospect Source type either from Add or from Friend' where id=27;
Update m_code set code_description='Events in the System like Create Client, Create Order used in workflow' where id=28;
Update m_code set code_description='Actions in the system like Send Email Send Sms etc in workflow' where id=29;
Update m_code set code_description='Used in Accounting' where id=31;
Update m_code set code_description='Used in Accounting' where id=32;
Update m_code set code_description='Used in Accounting' where id=33;
Update m_code set code_description='Used in Accounting' where id=34;
Update m_code set code_description='Used in Accounting' where id=35;
Update m_code set code_description='Depricated' where id=36;
Update m_code set code_description='Depricated' where id=37;
Update m_code set code_description='While closing the client record the Reason type' where id=38;
Update m_code set code_description='Depricated' where id=39;
Update m_code set code_description='Track the Order status via Business Process' where id=40;
Update m_code set code_description='Cas Adhoc Command definition linked to command_parameters' where id=41;
Update m_code set code_description='Used in the Logistics for the Item Quality like Good or Faulty' where id=42;
Update m_code set code_description='We have Order Extension it is used to define the no of day to extend' where id=43;
Update m_code set code_description='Reason for the Order Extension' where id=44;
Update m_code set code_description='Used in the ISP for one of the Service Provisioing parameter' where id=45;
Update m_code set code_description='Office are created and mapped to type' where id=46;
Update m_code set code_description='Asset Media Category need more info on this' where id=47;
Update m_code set code_description='While creating the ticket need to know from what source like phone or email' where id=48;
Update m_code set code_description='Define the IP Address type either Public or Private' where id=49;
