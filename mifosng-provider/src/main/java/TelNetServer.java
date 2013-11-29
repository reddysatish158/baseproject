import java.net.*;
import java.io.*;
import java.lang.*;
import java.io.*;
import java.util.*;

class TelnetServer
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket Soc=new ServerSocket(5217);
        while(true)
        {
            Socket CSoc=Soc.accept();
            AcceptTelnetClient ob=new AcceptTelnetClient(CSoc);
        }
    }
}
