import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.logistics.item.domain.StatusTypeEnum;


class Jasper
{
    public static void main(String args[]) throws Exception
    {

		Long i=new Long(1);
		Integer j=StatusTypeEnum.ACTIVE.getValue();
		   if(i.equals(StatusTypeEnum.ACTIVE.getValue().longValue())){
		System.out.println("tu");
		   }else	
			   System.out.println("false");
    }
}