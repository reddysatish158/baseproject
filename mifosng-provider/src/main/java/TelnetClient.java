import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.mifosplatform.infrastructure.core.service.FileUtils;


class Jasper
{
    public static void main(String args[]) throws Exception
    {

		String fileLocation = FileUtils.MIFOSX_BASE_DIR;

		/** Recursively create the directory if it does not exist **/
		if (!new File(fileLocation).isDirectory()) {
			new File(fileLocation).mkdirs();
		}
		
		String jpath = fileLocation+File.separator+"jasper"; //System.getProperty("user.home") + File.separator + "billing";
		String printInvoicedetailsLocation = fileLocation + File.separator + "Bill_" +1 + ".pdf";
		


		
		String jfilepath =jpath+File.separator+"Bill_Mainreport.jasper";
		@SuppressWarnings("rawtypes")
		Map<String, Object> parameters = new HashMap();
		String id = String.valueOf(10);
			parameters.put("param1", id);
			parameters.put("SUBREPORT_DIR",jpath+""+File.separator);
		   JasperPrint jasperPrint = JasperFillManager.fillReport(jfilepath,parameters);
			JasperExportManager.exportReportToPdfFile(jasperPrint,printInvoicedetailsLocation);
		
		
	
    }
    }