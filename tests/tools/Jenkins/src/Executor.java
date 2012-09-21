import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Executor extends Thread {

	private String[] commands;
	private String dir;
	private String output = "", error = ""; public String sent = "";
	private int exitValue;
	private String input;
	private boolean timedout = false;
	private int ulimit;
	public boolean exceptions = false;
	
	
	public Executor(String[] commands, String dir, String input, int ulimit) {
		super();
		this.commands = commands;
		this.dir = dir;
		this.input = input;
		this.ulimit = ulimit;
	}

	@Override
	public void run() {
		try {
			output = ""; error = "";
			ProcessBuilder pb = new ProcessBuilder(commands);
			pb.directory(new File(dir));
			MyCallable<Integer> callable = new MyCallable<Integer>(pb.start(), input, ulimit) {
		        public Integer call() throws Exception
		        {
		    		if (input != null && !input.equals(""))
		    		{
		    			OutputStream stream = p.getOutputStream();
		    			stream.write(input.getBytes());
		    			stream.flush();
		    			stream.close();
		    		}
		    		
		    		exitValue = p.waitFor();
		    		BufferedReader br = new BufferedReader(new InputStreamReader(
		    				p.getInputStream()));
		    		String line;
		    		output = "";
		    		while ((line = br.readLine()) != null) {
		    			output += line + "\n";
		    			line = "";
		    		}

		    		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		    		line = ""; error = "";
		    		while ((line = br.readLine()) != null) {
		    			error += line + "\n";
		    			line = "";
		    		}

		    		p.wait(timeout);
		    		return p.waitFor();
		        }};
	        exitValue = timedCall(callable, ulimit, TimeUnit.SECONDS);
		    output = callable.output;
		    error = callable.error;
		} catch (IOException e) {
		    exceptions = true;
		    error = e.getMessage();
		    System.out.println("Jenkins: " + e.getLocalizedMessage());
		    System.out.println("Command: " + commands);
		    e.printStackTrace();
		}
	}

	public String[] getCommands() {
		return commands;
	}

	public String getOutput() {
		return output;
	}

	public String getError() {
		return error;
	}

	public int getExitValue() {
		return exitValue;
	}
	
	public boolean getTimedOut()
	{
		return timedout;
	}
	
	@Override
	public String toString() {
		String commands = "";
		for(String cmd : this.commands)
			commands += cmd + " ";
		
		return "`" + commands.trim() + "` in directory: " + dir;
	}
	
	private static final ExecutorService THREAD_POOL 
    = Executors.newSingleThreadExecutor();

	private static <T> T timedCall(Callable<T> c, long timeout, TimeUnit timeUnit)
	    {
		    try {
				FutureTask<T> task = new FutureTask<T>(c);
			    THREAD_POOL.execute(task);
				return task.get(timeout, timeUnit);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    return null;
	    }
}

class MyCallable<T> implements Callable<T>
{
	Process p;
	String output = "";
	String error = "";
	String input = "";
	int timeout = 12000;
	
	public MyCallable(Process p1, String input, int timeout)
	{
		this.p = p1;
		this.input = input;
		this.timeout = timeout;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T call() throws Exception {
		p.wait(timeout);
        return (T) (Integer)p.waitFor();
	}
}
