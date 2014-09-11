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