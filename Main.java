package view;
import java.io.*;
import java.sql.Time;
import java.util.*;
import javax.swing.JOptionPane;
import utils.UtilsMethods;
import control.Court;
import enums.Gender;
import enums.Specialization;
import enums.Status;
import enums.Position;
import model.*;

public class Main {
    public static Court court = Court.getInstance();
    private static Map<String, Command> commands = new HashMap<>();

    public static void main(String[] args) throws IOException {
        loadCommands();  // Load commands
        loadDataFromCSV("INPUT.csv");  // Read CSV input
        save();  // Save court data
        // Open GUI after loading data
        LOGIN login = new LOGIN();
        login.setVisible(true);
        // Save data when the program ends
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    public static void loadCommands() {
     commands.put("addDepartment", (args) -> {
            Department department = new Department(
                    Integer.parseInt(args[0]), args[1], court.getRealEmployee(Integer.parseInt(args[2])),
                    args[3], Specialization.valueOf(args[4])
            );
            if (court.addDepartment(department))
                System.out.println("Added department: " + department.getNumber());
        });

        commands.put("addCourtroom", (args) -> {
			Courtroom courtroom = new Courtroom(Integer.parseInt(args[0]),court.getRealDepartment(Integer.parseInt(args[1])));
            if (court.addCourtroom(courtroom))
                System.out.println("Added courtroom: " + courtroom.getCourtroomNumber());
        });

        commands.put("addLawyer", (args) -> {
            Lawyer lawyer = new Lawyer(
                    Integer.parseInt(args[0]), args[1], args[2], UtilsMethods.parseDate(args[3]),
                    args[4], args[5], args[6], Gender.valueOf(args[7]), Specialization.valueOf(args[8]),
                    Integer.parseInt(args[9]), Double.parseDouble(args[10])
            );
            if (court.addLawyer(lawyer))
                System.out.println("Added lawyer: " + lawyer.getId());
        });

        commands.put("addJudge", (args) -> {
            Judge judge  = new Judge(Integer.parseInt(args[0]),args[1],args[2],UtilsMethods.parseDate(args[3])
					,args[4],args[5],args[6],Gender.valueOf(args[7]),Specialization.valueOf(args[8]),
					Integer.parseInt(args[9]),Double.parseDouble(args[10]),Integer.parseInt(args[11]));
		
            if (court.addJudge(judge))
                System.out.println("Added judge: " + judge.getId());
        });
 

        commands.put("addEmployee", (args) -> {
        	Employee employee = new Employee(Integer.parseInt(args[0]),args[1],args[2],UtilsMethods.parseDate(args[3])
					,args[4],args[5],args[6],Gender.valueOf(args[7]),UtilsMethods.parseDate(args[8]),
							Double.parseDouble(args[9]),Position.valueOf(args[10]));
				if(court.addEmployee(employee))
                System.out.println("Added employee "+employee.getId());
        });
        
        commands.put("addAccused", (args) -> {
            Accused accused = new Accused(
                    Integer.parseInt(args[0]), args[1], args[2], UtilsMethods.parseDate(args[3]),
                    args[4], args[5], args[6], Gender.valueOf(args[7]), args[8]
            );
            if (court.addAccused(accused))
                System.out.println("Added accused: " + accused.getId());
        });

        commands.put("addWitness", (args) -> {
            Witness witness = new Witness(
                    Integer.parseInt(args[0]), args[1], args[2], UtilsMethods.parseDate(args[3]),
                    args[4], args[5], args[6], Gender.valueOf(args[7])
            );
            if (court.addWitness(witness))
                System.out.println("Added witness: " + witness.getId());
        });
		
        commands.put("addFamilyCase", (args) -> {
        	FamilyCase casee = new FamilyCase(court.getRealAccused(Integer.parseInt(args[0])),UtilsMethods.parseDate(args[1])
    				,Status.valueOf(args[2]),Specialization.valueOf(args[3]),
    				court.getRealLawyer(Integer.parseInt(args[4])),null,(Person)court.getRealWitness(Integer.parseInt(args[5])),args[6]);
    		
            if (court.addCase(casee))
                System.out.println("Added case: " + casee.getCode());
        });
    	
        commands.put("addCriminalCase", (args) -> {
        	CriminalCase casee = new CriminalCase(court.getRealAccused(Integer.parseInt(args[0])),UtilsMethods.parseDate(args[1])
    				,Status.valueOf(args[2]),Specialization.valueOf(args[3]),
    				court.getRealLawyer(Integer.parseInt(args[4])),null,(Person)court.getRealWitness(Integer.parseInt(args[5])),args[6],args[7]);
    		
            if (court.addCase(casee))
                System.out.println("Added case: " + casee.getCode());
        });
       
        commands.put("addFinancialCase", (args) -> {
        	 
            FinancialCase casee = new FinancialCase(court.getRealAccused(Integer.parseInt(args[0])),UtilsMethods.parseDate(args[1])
    				,Status.valueOf(args[2]),Specialization.valueOf(args[3]),
    				court.getRealLawyer(Integer.parseInt(args[4])),null,Double.parseDouble(args[5]),args[6]);
    		
            if (court.addCase(casee))
                System.out.println("Added case: " + casee.getCode());
        });
        commands.put("addDocument", (args) -> {
        	Document document = new Document(args[0],args[1],UtilsMethods.parseDate(args[2])
					,court.getRealCase((args[3])));

			if(court.addDocument(document))
                System.out.println("Added document "+document.getCode());
        });
       
        commands.put("addMeeting", (args) -> {
       	 
        	Meeting meeting = new Meeting(UtilsMethods.parseDate(args[0]),
					Time.valueOf(args[1])
					,court.getRealCourtroom(Integer.parseInt(args[2])),
					court.getRealCase(args[3]));
			if(court.addMeeting(meeting))
                System.out.println("Added meeting: " +meeting.getMeetingID());
        });
        commands.put("addTestimony", (args) -> {
        	Testimony testimony = new Testimony(court.getRealCase(args[0]),args[1],
					court.getRealWitness(Integer.parseInt(args[2])));
			if(court.addTestimony(testimony))
                System.out.println("Added tetimony: " +testimony.getTestimonyID());
        });
 
        commands.put("addAppeal", (args) -> {
        	Appeal appeal = new Appeal(args[0],UtilsMethods.parseDate(args[1])
					,court.getRealVerdict(Integer.parseInt(args[2])),
					court.getRealVerdict(Integer.parseInt(args[3])));
			
			if(court.addAppeal(appeal))
                System.out.println("Added appeal "+appeal.getAppealID());
        });
        
        commands.put("addVerdict", (args) -> {
        	Verdict verdict = new Verdict(args[0],UtilsMethods.parseDate(args[1])
					,court.getRealJudge(Integer.parseInt(args[2])),
					court.getRealCase(args[3]),null);
			if(court.addVerdict(verdict))
                System.out.println("Added verdict "+verdict.getVerdictID());
        });
        

        commands.put("addLawyerToDepartment", (args) -> {
            Department department = court.getRealDepartment(Integer.parseInt(args[1]));
            Lawyer lawyer = court.getAllLawyers().get(Integer.parseInt(args[0]));
            if (department != null && lawyer != null) {
                court.addLawyerToDepartment(department, lawyer);
                System.out.println("Added lawyer " + lawyer.getId() + " to department " + department.getNumber());
            } else {
                System.out.println("Failed to add lawyer to department");
            }
        });

        commands.put("addJudgeToDepartment", (args) -> {
            Department department = court.getRealDepartment(Integer.parseInt(args[1]));
            Judge judge = (Judge) court.getAllLawyers().get(Integer.parseInt(args[0])); // Ensure it's cast properly
            if (department != null && judge != null) {
                court.addJudgeToDepartment(department, judge);
                System.out.println("Added judge " + judge.getId() + " to department " + department.getNumber());
            } else {
                System.out.println("Failed to add judge to department");
            }
        });

        commands.put("addEmployeeToDepartment", (args) -> {
            Department department = court.getRealDepartment(Integer.parseInt(args[1]));
            Employee employee = court.getAllEmployees().get(Integer.parseInt(args[0]));
            if (department != null && employee != null) {
                court.addEmployeeToDepartment(department, employee);
                System.out.println("Added employee " + employee.getId() + " to department " + department.getNumber());
            } else {
                System.out.println("Failed to add employee to department");
            }
        });

    }

    public static void loadDataFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                
                String commandName = parts[0];
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);

                if (commands.containsKey(commandName)) {
                    commands.get(commandName).execute(args);
                } else {
                    System.out.println("Unknown command: " + commandName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream("Court.ser");
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(court);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private interface Command {
        void execute(String... args);
    }
}
