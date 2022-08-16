/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
package bgu.spl.mics.application;
import java.io.FileReader;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws FileNotFoundException {

        //Elements of the program
        LinkedBlockingQueue<Student> students = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<GPU> GPUS = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<CPU> CPUS = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<ConfrenceInformation> conferences = new LinkedBlockingQueue<>();
        long ti=0;
        long du=0;

        Cluster clus = Cluster.getInstance();

        //Retrieving the InputFile and start reading it
        try {
            Object obj = new JSONParser().parse(new FileReader(args[0]));
            JSONObject json = (JSONObject) obj;
            Object curr;

            //Creating all of the Students
            JSONArray Student = (JSONArray) json.get("Students");
            Iterator it = Student.iterator();
            JSONObject model;
            Student s;
            Model m;
            String name;
            String dep;
            String status;
            String model_name;
            String model_type;
            Data data;
            int model_size;

            while (it.hasNext()) {
                curr = it.next();
                JSONObject curr_student = (JSONObject) curr;
                name = (String) curr_student.get("name");
                dep = (String) curr_student.get("department");
                status = (String) curr_student.get("status");
                s = new Student(name, dep, status);
                students.add(s);

                //Creating the Models for the student
                JSONArray findModels = (JSONArray) curr_student.get("models");
                Iterator itr5 = findModels.iterator();
                while (itr5.hasNext()) {
                    model = (JSONObject) itr5.next();
                    model_name = (String) model.get("name");
                    model_type = (String) model.get("type");
                    model_size =(int)(long) model.get("size");
                    data = new Data(model_type, 0, (int) model_size);
                    m = new Model(model_name, data, s);
                    s.addModel(m);
                }
            }

            //Creating all of the GPUS
            JSONArray gpu = (JSONArray) json.get("GPUS");
            Iterator it1 = gpu.iterator();
            while (it1.hasNext()) {
                curr = it1.next();
                GPU.Type t =null ;
                if (curr.toString().equals("RTX3090"))
                    t = GPU.Type.RTX3090;
                if (curr.toString().equals("RTX2080"))
                    t = GPU.Type.RTX2080;
                if (curr.toString().equals("GTX1080"))
                    t = GPU.Type.GTX1080;
                GPU g = new GPU(t);
                GPUS.add(g);
            }
            clus.setGPUS(GPUS);

            //Creating all of the CPUS
            JSONArray cpu = (JSONArray) json.get("CPUS");
            Iterator it2 = cpu.iterator();
            while (it2.hasNext()) {
                curr = it2.next();
                CPU c = new CPU((int)(long)curr);
                CPUS.add(c);
            }
            clus.setCPUS(CPUS);


            //Creating all of the confrences
            JSONArray conference = (JSONArray)json.get("Conferences");
            Iterator it3 = conference.iterator();
            ConfrenceInformation newConference;
            long date;
            while (it3.hasNext()) {
                curr = it3.next();
                JSONObject conf = (JSONObject)curr;
                name = (String) conf.get("name");
                date =(long)conf.get("date");
                newConference = new ConfrenceInformation(name,(int) date);
                conferences.add(newConference);
            }

            ti = (long)json.get("TickTime");
            du = (long)json.get("Duration");


        } catch (Exception e) {}


        //now we can start creating the Threads and run them !
        LinkedBlockingQueue<Thread> l = new LinkedBlockingQueue<>();
        Iterator<Student> it_student = students.iterator();
        Iterator<GPU> it_gpu = GPUS.iterator();
        Iterator<CPU> it_cpu = CPUS.iterator();
        Iterator<ConfrenceInformation> it_con = conferences.iterator();

        //creating StudentServices threads and run them
        while (it_student.hasNext())
        {
            Student s = it_student.next();
            Thread t = new Thread(new StudentService(s.getName()+"Service", s));
            l.add(t);
        }

        //creating GPUServices threads and run them
        Integer i=0;
        while (it_gpu.hasNext())
        {
            GPU g = it_gpu.next();
            Thread t = new Thread(new GPUService("GPU"+i.toString(),g ));
            l.add(t);
            i=i+1;
        }

        //creating CPUServices threads and run them
        i=0;
        while (it_cpu.hasNext())
        {
            CPU c = it_cpu.next();
            Thread t = new Thread(new CPUService("CPU"+i.toString(),c ));
            l.add(t);
            i=i+1;

        }

        //creating ConfrenceServices threads and run them
        while(it_con.hasNext())
        {
            ConfrenceInformation c = it_con.next();
            Thread t = new Thread(new ConferenceService(c.getName()+"Service", c));
            l.add(t);
        }
        //creating TimeService and run it
        l.add(new Thread(new TimeService(ti, du)));



        // Now we wish to wait in order for the threads to terminate before MainThread
        Iterator<Thread> it_start = l.iterator();
        while (it_start.hasNext()) {
            Thread th = it_start.next();
            th.start();
        }

        Iterator<Thread> it_end = l.iterator();
        try {
            while (it_end.hasNext()) {
                Thread th = it_end.next();
                th.join();
            }
            }catch (InterruptedException e){}

        //example
        Iterator<Student> it_q = students.iterator();
        while (it_q.hasNext()) {
            Student cur_s = it_q.next();
            Iterator<Model> it_e = cur_s.getModels().iterator();
            while (it_e.hasNext()){
                Model cur_m = it_e.next();
            }
        }



        //output

        //generate output file
        PrintWriter output = new PrintWriter("./outputfile.txt");
        //conferences
        String conf = "conferences: ["+"\n";
        //students + train models
        for (Student s : students) {
            String student_s = "Student name: " + s.getName() + "\n" + "department: " + s.getDepartment() + "\n" + "status: " + s.getStatus() + "\n" + "publications: " + s.getPublications() + "\n" + "papersRead: " + s.getPublications() + "\n";
            String trained_models = "trainedModels: ["+"\n";
            for (Model m :  s.getModels()) {
                if(m.getStatus().equals(Model.status.Tested)){
                    trained_models = trained_models + "{" + "\n" + "name: "+m.getName() + "\n" + "data: " + "\n" + "   type: " + m.getData().getType() +"\n" + "   size: " + m.getData().size() + "\n" +  "status: " + m.getStatus() + "\n" +"result: "+ m.getTestResult() + "\n" + "}" + "\n";
                }
            }
            trained_models = trained_models + "]" + "\n";
            output.println(student_s);
            output.println(trained_models);
        }
        for(ConfrenceInformation c : conferences){
            conf = conf + "name: " + c.getName() + "\n" + "date: " + c.getDate() + "\n" + "publications:[" +"\n";
            for (Model m : c.getResults()){
                conf = conf + "name: "+ m.getName() + "\n" + "data: " + "\n" + "   type: " + m.getData().getType() + "\n" + "   size: " + m.getData().size() + "\n" + "status: " + m.getStatus() +"\n" + "results: " + m.getTestResult() + "\n" + "\n";  //diffrence between lines
            }
        }
        conf = conf + "]" + "\n";
        output.println(conf);
        Statistics statistics = clus.getStatistics();
        String cpu_time = "cpuTimeUsed:" + statistics.getCPUnitsTime();
        String gpu_time = "gpuTimeUsed:" + statistics.getGPUnitsTime();
        String batchesProcessed = "batchesProcessed:" + statistics.getProcessBatchCPU();
        output.println(cpu_time);
        output.println(gpu_time);
        output.println(batchesProcessed);
        output.close();
    }
}
