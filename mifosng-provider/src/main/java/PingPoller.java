import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingPoller
{
    public static void main(String[] args)
    {
        System.out.println("Ping Poller Starts...");
        String ipAddress = "localhost";
        try
        {
            InetAddress inet = InetAddress.getByName(ipAddress);
            System.out.println("Sending Ping Request to " + ipAddress);
            boolean status = inet.isReachable(5000); //Timeout = 5000 milli seconds
            if (status)
            {
                System.out.println("Status : Host is reachable");
            }
            else
            {
                System.out.println("Status : Host is not reachable");
            }
        }
        catch (UnknownHostException e)
        {
            System.err.println("Host does not exists");
        }
        catch (IOException e)
        {
            System.err.println("Error in reaching the Host");
        }
    }
}
