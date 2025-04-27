package control;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import enums.Gender;
import enums.Position;
import enums.Specialization;
import enums.Status;
import model.Accused;
import model.Appeal;
import model.Courtroom;
import model.CriminalCase;
import model.Department;
import model.Document;
import model.Employee;
import model.FamilyCase;
import model.FinancialCase;
import model.Judge;
import model.Lawyer;
import model.Meeting;
import model.Testimony;
import model.Verdict;
import model.Witness;
import model.Case;
import utils.MyFileLogWriter;
import utils.UtilsMethods;

public class Court implements Serializable{
	private static Court instance ;
	private HashMap<Integer,Lawyer>allLawyers;
	private HashMap<Integer,Accused>allAccuseds;
	private HashMap<Integer,Employee>allEmployees;
	private HashMap<Integer,Witness>allWitnesses;
	private HashMap<Integer,Testimony>allTestimonies;
	private HashMap<Integer,Department>alldepartments;
	private HashMap<Integer,Courtroom>allCourtrooms;
	private HashMap<String,Case>allCases;
	private HashMap<Integer,Meeting>allMeetings;
	private HashMap<Integer,Verdict>allVerdicts;
	private HashMap<Integer,Appeal>allAppeals;
	private HashMap<Integer,Document>allDocuments;

	public final static Date TODAY=UtilsMethods.parseDate("26/11/2024");
	
	//private constructor
	private Court() {
		super();
		this.allLawyers = new HashMap<>();
		this.allAccuseds = new HashMap<>();
		this.allEmployees = new HashMap<>();
		this.allWitnesses = new HashMap<>();
		this.allTestimonies = new HashMap<>();
		this.alldepartments = new HashMap<>();
		this.allCourtrooms = new HashMap<>();
		this.allCases = new HashMap<>();
		this.allMeetings = new HashMap<>();
		this.allVerdicts = new HashMap<>();
		this.allAppeals = new HashMap<>();
		this.allDocuments = new HashMap<>();
	}
	//getInstance
	public static Court getInstance() {
		if (instance==null) {
			instance = new Court();
		}
		return instance;
	}
	
	// add methods
	
	public boolean addEmployeeToDepartment(Department department , Employee employee) {
		if(department == null || employee == null) return false;
		if(alldepartments.containsKey(department.getNumber()) && allEmployees.containsKey(employee.getId())) {
			return department.addEmployee(employee);
		}
		return false;
	}
	
	public boolean addLawyerToDepartment(Department department , Lawyer lawyer) {
		if(department == null || lawyer == null) return false;
		if(alldepartments.containsKey(department.getNumber()) && allLawyers.containsKey(lawyer.getId())) {
			return department.addLawyer(lawyer);
		}
		return false;
	}
	
	public boolean addJudgeToDepartment(Department department , Judge judge) {
		if(department == null || judge == null) return false;
		if(alldepartments.containsKey(department.getNumber()) && allLawyers.containsKey(judge.getId())) {
			return department.addJudge(judge);
		}
		return false;
	}
	
	
	public boolean addDepartment(Department department) {
		if(department == null) return false;
		if(alldepartments.containsKey(department.getNumber())) return false;
		alldepartments.put(department.getNumber(),department);
		return true;
	}
	
	public boolean addCourtroom(Courtroom courtroom) {
		if(courtroom==null) return false;
		if(allCourtrooms.containsKey(courtroom.getCourtroomNumber())) return false;
		if(alldepartments.containsKey(courtroom.getDepartment().getNumber()))
			alldepartments.get(courtroom.getDepartment().getNumber()).addCourtroom(courtroom);
		allCourtrooms.put(courtroom.getCourtroomNumber(),courtroom);
		return true;
	}
	
	
	public boolean addLawyer(Lawyer lawyer) {
		if(lawyer==null) return false;
		if(allLawyers.containsKey(lawyer.getId())) return false;
		allLawyers.put(lawyer.getId(),lawyer);
		return true;
	}
	
	public boolean addJudge(Judge judge) {
		if(judge==null) return false;
		if(allLawyers.containsKey(judge.getId())) return false;
		allLawyers.put(judge.getId(),judge);
		return true;
	}
	
	
	public boolean addEmployee(Employee employee) {
		if(employee==null) return false;
		if(allEmployees.containsKey(employee.getId())) return false;
		for(Department dep:alldepartments.values())
			if(employee.getDepartments().contains(dep)) {
				dep.addEmployee(employee);
				break;
			}
		allEmployees.put(employee.getId(),employee);
		return true;
	}
	
	
	public boolean addDocument(Document doc) {
		if(doc==null) return false;
		if(allDocuments.containsKey(doc.getCode())) return false;
		for(Case curCase : allCases.values()) {
			if(doc.getCasee().equals(curCase)) {
				curCase.addDocument(doc);
				break;
			}
		}
		allDocuments.put(doc.getCode(),doc);
		return true;
	}
	
	public boolean addVerdict(Verdict verdict) {
		if(verdict==null) return false;
		if(allVerdicts.containsKey(verdict.getVerdictID())) return false;
		for(Case curCase : allCases.values()) {
			if(verdict.getCasee().equals(curCase)) {
				curCase.setVerdict(verdict);
				curCase.setCaseStatus(Status.finished);
				break;
			}
		}
		if(verdict.getAppeal()!=null) {
			allAppeals.put(verdict.getAppeal().getAppealID(),verdict.getAppeal());
		}
		allVerdicts.put(verdict.getVerdictID(),verdict);
		return true;
	}
	
	public boolean addAccused(Accused accused) {
		if(accused==null) return false;
		if(allAccuseds.containsKey(accused.getId())) return false;
		allAccuseds.put(accused.getId(),accused);
		return true;
	}
	
	public boolean addAppeal(Appeal appeal) {
		if(appeal==null) return false;
		if(allAppeals.containsKey(appeal.getAppealID())) return false;
		if(!allVerdicts.containsKey(appeal.getCurrentVerdict().getVerdictID())) 
			allVerdicts.put(appeal.getCurrentVerdict().getVerdictID(),appeal.getCurrentVerdict());
		if(!allVerdicts.containsKey(appeal.getNewVerdict().getVerdictID())) 
			allVerdicts.put(appeal.getNewVerdict().getVerdictID(),appeal.getNewVerdict());
		allVerdicts.get(appeal.getCurrentVerdict().getVerdictID()).setAppeal(appeal);
		allCases.get(appeal.getCurrentVerdict().getCasee().getCode()).setVerdict(appeal.getNewVerdict());
		allAppeals.put(appeal.getAppealID(),appeal);
		return true;
	}
	
	public boolean addTestimony(Testimony testimony) {
	    if (testimony == null) {
	        return false;
	    }
	    if (allTestimonies.containsKey(testimony.getTestimonyID())) {
	        return false;
	    }

	    if (testimony.getWitness() == null) {
	        return false;
	    }

	    if (!allWitnesses.containsKey(testimony.getWitness().getId())) {
	        allWitnesses.put(testimony.getWitness().getId(), testimony.getWitness());
	    }

	    if (allCases.get(testimony.getCasee().getCode()) == null) {
	        return false;
	    }

	    allWitnesses.get(testimony.getWitness().getId()).addTestimony(testimony);

	    allCases.get(testimony.getCasee().getCode()).addTestimony(testimony);

	    allTestimonies.put(testimony.getTestimonyID(), testimony);

	    return true;
	}

	
	public boolean addWitness(Witness witness) {
		if(witness==null) return false;
		if(allWitnesses.containsKey(witness.getId())) return false;
		for(Testimony tes:witness.getTestimoniesList()) {
			if(!allTestimonies.containsKey(tes.getTestimonyID())) 
				allTestimonies.put(tes.getTestimonyID(),tes);
		}
		for(Case c : allCases.values()) {
			for(Testimony tes: witness.getTestimoniesList()) {
				if(tes.getCasee().equals(c)) c.addTestimony(tes);
			}
		}
		allWitnesses.put(witness.getId(),witness);
		return true;
	}
	
	public boolean addMeeting(Meeting meet) {
		if(meet==null) return false;
		if(allMeetings.containsKey(meet.getMeetingID())) return false;
		if(!allCases.containsKey(meet.getCasee().getCode())) allCases.put(meet.getCasee().getCode(),meet.getCasee());
		if(!allCourtrooms.containsKey(meet.getCourtroom().getCourtroomNumber())) allCourtrooms.put(meet.getCourtroom().getCourtroomNumber(),meet.getCourtroom());
		for(Meeting m : allMeetings.values())
			if(m.getCourtroom().equals(meet.getCourtroom())&&
					m.getHour().equals(meet.getHour())&&
					m.getMeetingDate().equals(meet.getMeetingDate())) {
				return false;
			}
			
		if(meet.getMeetingDate().before(meet.getCasee().getOpenedDate())) {
			return false;
		}
		for(Case c:allCases.values()) {
			if(c.equals(meet.getCasee())) c.addMeeting(meet);
		}
		allMeetings.put(meet.getMeetingID(),meet);
		return true;
	}
	
	public boolean addCase(Case c) {
		if(c==null) return false;
		if(allCases.containsKey(c.getCode())) return false;
		if(c.getLawyer()!=null) {
			if(!allLawyers.containsKey(c.getLawyer().getId())) allLawyers.put(c.getLawyer().getId(),c.getLawyer());
			allLawyers.get(c.getLawyer().getId()).addCase(c);
		}
		if(!allAccuseds.containsKey(c.getAccused().getId())) allAccuseds.put(c.getAccused().getId(),c.getAccused());
		allAccuseds.get(c.getAccused().getId()).addCase(c);
		if(c.getVerdict()!=null) {
			if(!allVerdicts.containsKey(c.getVerdict().getVerdictID())) allVerdicts.put(c.getVerdict().getVerdictID(),c.getVerdict());
			allVerdicts.get(c.getVerdict().getVerdictID()).setCasee(c);
		}
		for(Meeting meet:c.getMeetingsList()) {
			if(!allMeetings.containsKey(meet.getMeetingID())) allMeetings.put(meet.getMeetingID(),meet);
		}
		for(Document doc:c.getDocumentsList()) {
			if(!allDocuments.containsKey(doc.getCode())) allDocuments.put(doc.getCode(),doc);
		}
		for(Testimony tes:c.gettestimoniesList()) {
			allWitnesses.put(tes.getWitness().getId(),tes.getWitness());
			allTestimonies.put(tes.getTestimonyID(), tes);
		}
		allCases.put(c.getCode(),c);
		return true;
	}
	public boolean addFinancialCase(FinancialCase c) {
		return addCase(c);
	}
	
	public boolean addCriminalCase(CriminalCase c) {
		return addCase(c);
	}
	
	public boolean addFamilyCase(FamilyCase c) {
		return addCase(c);
	}
	
	//getReal methods
	public Department getRealDepartment(int number) {
		return alldepartments.get(number);
	}
	
	public Courtroom getRealCourtroom(int code) {
		return allCourtrooms.get(code);
	}
	
	public Lawyer getRealLawyer(int ID) {
		return allLawyers.get(ID);
	}
	
	public Judge getRealJudge(int ID) {
		return (Judge)allLawyers.get(ID);

	}
	public Accused getRealAccused(int ID) {
		return allAccuseds.get(ID);
	}
	
	public Employee getRealEmployee(int ID) {
		return allEmployees.get(ID);
	}

	public Document getRealDocument(int code) {
		return allDocuments.get(code);
	}
	
	public Verdict getRealVerdict(int number) {
		return allVerdicts.get(number);
	}
	
	public Testimony getRealTestimony(int code) {
		return allTestimonies.get(code);
	}
	
	
	public Witness getRealWitness(int ID) {
		return allWitnesses.get(ID);
	}

	public Appeal getRealAppeal(int number) {
		return allAppeals.get(number);
	}
	
	public Meeting getRealMeeting(int number) {
		return allMeetings.get(number);
	}
	
	public Case getRealCase(String code) {
		return allCases.get(code);
	}
	
	public FinancialCase getRealFinancialCase(String code) {
		return (FinancialCase) allCases.get(code);
	}
	
	public CriminalCase getRealCriminalCase(String code) {
		return (CriminalCase)allCases.get(code);
	}
	
	public FamilyCase getRealFamilyCase(String code) {
		return (FamilyCase)allCases.get(code);
	}
	
	//remove methods
	public boolean removeDepartment(Department department) {
		if(department == null) return false;
		if(!alldepartments.containsKey(department.getNumber())) return false;
		for(Employee e: department.getEmployees())
			e.removeDepartment(department);
		for(Lawyer l : department.getLawyers())
			l.setDepartment(null);
		for(Courtroom c : department.getCourtrooms())
			c.setDepartment(null);

		alldepartments.remove(department.getNumber());
		return true;
	}
	
	public boolean removeCourtroom(Courtroom courtroom) {
		if(courtroom==null) return false;
		if(!allCourtrooms.containsKey(courtroom.getCourtroomNumber())) return false;
		for(Department dep:alldepartments.values())
			if(dep.equals(courtroom.getDepartment())) {
				dep.removeCourtroom(courtroom);
				break;
			}
		allCourtrooms.remove(courtroom.getCourtroomNumber());
		return true;
	}
	
	public boolean removeLawyer(Lawyer lawyer) {
		if(lawyer==null) return false;
		if(!allLawyers.containsKey(lawyer.getId())) return false;
		for(Department dep:alldepartments.values())
			if(dep.equals(lawyer.getDepartment())) {
				dep.removeLawyer(lawyer);
				break;
			}
		allLawyers.remove(lawyer.getId());
		return true;
	}
	
	public boolean removeJudge(Judge judge) {
		if(judge==null) return false;
		if(!allLawyers.containsKey(judge.getId())) return false;
		for(Department dep:alldepartments.values())
			if(dep.equals(judge.getDepartment())) {
				dep.removeJudge(judge);
				break;
			}
		allLawyers.remove(judge.getId());
		return true;
	}
		
	public boolean removeEmployee(Employee employee) {
		if(employee==null) return false;
		if(!allEmployees.containsKey(employee.getId())) return false;
		for(Department dep:employee.getDepartments()) {
			dep.removeEmployee(employee);
			break;
		}
		allEmployees.remove(employee.getId());
		return true;
	}
	
	public boolean removeDocument(Document doc) {
		if(doc==null) return false;
		if(!allDocuments.containsKey(doc.getCode())) return false;
		for(Case curCase : allCases.values()) {
			if(doc.getCasee().equals(curCase)) {
				curCase.removeDocument(doc);
				break;
			}
		}
		allDocuments.remove(doc.getCode());
		return true;
	}
	
	public boolean removeVerdict(Verdict verdict) {
	    if (verdict == null) return false;
	    if (!allVerdicts.containsKey(verdict.getVerdictID())) return false;
	    for (Case curCase : allCases.values()) {
	        if (verdict.getCasee() != null && verdict.getCasee().equals(curCase)) {
	            curCase.setVerdict(null);
	            break; 
	        }
	    }
	    List<Integer> keysToRemove = new ArrayList<>();
	    for (Map.Entry<Integer, Appeal> entry : allAppeals.entrySet()) {
	        Appeal a = entry.getValue();

	        if ((a != null && a.getCurrentVerdict() != null && verdict.equals(a.getCurrentVerdict())) ||
	            (a != null && a.getNewVerdict() != null && verdict.equals(a.getNewVerdict()))) {
	            keysToRemove.add(entry.getKey());
	        }
	    }
	    for (Integer key : keysToRemove) {
	        allAppeals.remove(key);
	    }
	    allVerdicts.remove(verdict.getVerdictID());
	    return true;
	}
	
	public boolean removeAppeal(Appeal appeal) {
		if(appeal==null) return false;
		if(!allAppeals.containsKey(appeal.getAppealID())) return false;		
		for(Verdict a : allVerdicts.values()) {
			if(a.getAppeal().equals(appeal)) a.setAppeal(null);
		}
		allAppeals.remove(appeal.getAppealID());
		return true;
	}
	
	public boolean removeTestimony(Testimony testimony) {
		if(testimony==null) return false;
		if(!allTestimonies.containsKey(testimony.getTestimonyID())) return false;
		for(Witness wit:allWitnesses.values()) {
			if(wit.equals(testimony.getWitness()))
			{
				wit.removeTestimony(testimony);
				break;
			}
		}
		allTestimonies.remove(testimony.getTestimonyID());
		return true;
	}
	
	public boolean removeWitness(Witness witness) {
	    if (witness == null) return false;
	    if (!allWitnesses.containsKey(witness.getId())) return false;
	    for (Case c : allCases.values()) {
	    	for(Testimony tes :witness.getTestimoniesList()) {
		        c.removeTestimony(tes);
		        allTestimonies.remove(tes.getTestimonyID());
		    }
	    }
	    for(Integer i : allTestimonies.keySet()) {
	        Testimony t = allTestimonies.get(i);
	        if (t.getWitness().equals(witness)) {
	            allTestimonies.remove(i);
	        }
	    }
	    allWitnesses.remove(witness.getId());
	    return true;
	}
	
	public boolean removeMeeting(Meeting meet) {
		if(meet==null) return false;
		if(!allMeetings.containsKey(meet.getMeetingID())) return false;
		for(Case c:allCases.values()) {
			if(c.equals(meet.getCasee())) c.removeMeeting(meet);
		}
		allMeetings.remove(meet.getMeetingID());
		return true;
	}
	
	public boolean removeAccused(Accused accused) {
	    if (accused == null) return false;
	    if (!allAccuseds.containsKey(accused.getId())) return false;
	    ArrayList<Case> casesToRemove = new ArrayList<>();
	    for (Case c : allCases.values()) {
	        if (accused.getCases().contains(c)) {
	            casesToRemove.add(c);
	        }
	    }
	    for(Case cc : casesToRemove)
	    	allCases.remove(cc.getCode());
	    allAccuseds.remove(accused.getId());
	    return true;
	}
	
	public boolean removeCase(Case c) {
	    if (c == null) return false;
	    if (!allCases.containsKey(c.getCode())) return false;

	    for (Lawyer l : allLawyers.values()) {
	        if (l.getCasesHandled().contains(c)) {
	            l.removeCase(c);
	        }
	    }

	    for (Accused a : allAccuseds.values()) {
	        if (a.getCases().contains(c)) {
	            a.removeCase(c);
	        }
	    }

	    for (int i = allVerdicts.size() - 1; i >= 0; i--) {
	        Verdict v = allVerdicts.get(i);
	        if (v != null && v.getCasee().equals(c)) {
	            for (int j = allAppeals.size() - 1; j >= 0; j--) {
	                Appeal a = allAppeals.get(j);
	                if (v.getAppeal() != null && v.getAppeal().equals(a)) {
	                    allAppeals.remove(j);
	                }
	            }
	            allVerdicts.remove(i);
	        }
	    }

	    for(Meeting meet : c.getMeetingsList()) {
	        allMeetings.remove(meet.getMeetingID());
	        c.getMeetingsList().remove(meet);
	    }

	    for(Document doc : c.getDocumentsList()) {	
	        allDocuments.remove(doc.getCode());
	        c.getDocumentsList().remove(doc);
	    }

	    for(Testimony tes:c.gettestimoniesList()) {
            allTestimonies.remove(tes.getTestimonyID());
            allWitnesses.get(tes.getWitness().getId()).removeTestimony(tes);
	    }
	    allCases.remove(c.getCode());
	    return true;
	}
	
	public boolean removeFamilyCase(FamilyCase c) {
	    return removeCase(c);
	}
	
	public boolean removeFinancialCase(FinancialCase c) {
	    return removeCase(c);
	}
	
	public boolean removeCriminalCase(CriminalCase c) {
	    return removeCase(c);
	}

	//Queries
	public Courtroom getMaxCasesRoom() {
		Courtroom toRet=null ;
		int max=0 ;
		ArrayList<Case> cases;
		for(Courtroom c : allCourtrooms.values()) {
			cases = new ArrayList<>();
			for(Meeting m :allMeetings.values()) {
				if(m.getCourtroom().equals(c)) {
					if(!cases.contains(m.getCasee())) cases.add(m.getCasee());
				}
			}
			if(max<cases.size()) {
				max=cases.size();
				toRet=c;
			}
		}
		return toRet;
	}
	
	public int howManyCasesBefore(Date date) {
		int count=0;
		for(Case casee:allCases.values()) {
			if(casee.getVerdict()!=null)
				if(casee.getVerdict().getIssusedDate().before(date)) {
					count++;
				}
		}
		return count;
	}
	
	
	public Accused theSameJudge() {
	    for(Appeal appeal : allAppeals.values())
	    {
	    	if(appeal.getCurrentVerdict()!=null &&
	    		appeal.getNewVerdict()!=null &&
	    		appeal.getCurrentVerdict().getJudge().equals(appeal.getNewVerdict().getJudge())) {
	    			return appeal.getCurrentVerdict().getCasee().getAccused();

	    	}
	    	
	    }
	    return null;
	}

	
	public int differenceBetweenTheLongestAndShortestCase(Lawyer lawyer) {
	    Integer longestCase = Integer.MIN_VALUE;
	    Integer shortestCase = Integer.MAX_VALUE;

	    if (lawyer == null || lawyer.getCasesHandled() == null || lawyer.getCasesHandled().isEmpty()) {
	        return 0; 
	    }

	    for (Case c : lawyer.getCasesHandled()) {
	        if (c.getMeetingsList() != null && !c.getMeetingsList().isEmpty()) {
	        	Date firstMeet = c.getMeetingsList().iterator().next().getMeetingDate();
	            Date lastMeet = c.getMeetingsList().iterator().next().getMeetingDate();
	            // Iterate through the meetings to find the earliest and latest dates
	            for (Meeting m : c.getMeetingsList()) {
	                if (m.getMeetingDate().before(firstMeet)) {
	                    firstMeet = m.getMeetingDate();
	                }
	                if (m.getMeetingDate().after(lastMeet)) {
	                    lastMeet = m.getMeetingDate();
	                }
	            }

	            // Calculate the duration in days
	            int duration = (int) ((lastMeet.getTime() - firstMeet.getTime()) / (1000 * 60 * 60 * 24));

	            if (duration > longestCase) longestCase = duration;
	            if (duration < shortestCase) shortestCase = duration;
	        }
	    }

	    if (longestCase == Integer.MIN_VALUE || shortestCase == Integer.MAX_VALUE) {
	        return 0;
	    }

	    return longestCase - shortestCase;
	}

	
	public void printMostExperiencedJudge() {
		Judge oldestOne =null,judge=null;
		double oldestAge=Double.MIN_VALUE;
		for(Lawyer l:allLawyers.values()) {
			if(l instanceof Judge) {
				judge = (Judge) l;
				if(oldestAge<judge.getExperienceYear()) {
					oldestAge=judge.getExperienceYear();
					oldestOne=judge;
				}
				
			}
			
		}
		MyFileLogWriter.println("The Most Experienced Judge is : "+oldestOne);

	}
	
	public Accused getAccusedWithMaxCasesByLawyers(Specialization caseType) {
	    Accused result = null;
	    int maxCases = 0;

	    for (Integer i : allAccuseds.keySet()) {
	        Accused accused = allAccuseds.get(i);
	        int totalCases = 0;
	        for(Case c : accused.getCases()) {
	        	Lawyer lawyer = c.getLawyer();
	        	for(Case c2 :lawyer.getCasesHandled()) {
	        		if(c2.getCaseType() == caseType) totalCases++;
	        	}
	        }

	        if (totalCases > maxCases) {
	            maxCases = totalCases;
	            result = accused;
	        }
	    }
	    return result;
	}

	
	public Witness findWitnessWithMostRepetitiveWordsInTestimony() {
	    Witness resultWitness = null;
	    int maxRepeats = 0;

	    for (Witness witness : allWitnesses.values()) {
	        for (Testimony testimony : witness.getTestimoniesList()) {
	            String[] words = testimony.getTestimonyContent().split("\\s+");
	            int localMaxRepeats = 0;

	            for (int i = 0; i < words.length; i++) {
	                int count = 1;
	                for (int j = i + 1; j < words.length; j++) {
	                    if (words[i].equalsIgnoreCase(words[j])) {
	                        count++;
	                    }
	                }

	                if (count > localMaxRepeats) {
	                    localMaxRepeats = count;
	                }
	            }

	            if (localMaxRepeats > maxRepeats) {
	                maxRepeats = localMaxRepeats;
	                resultWitness = witness;
	            }
	        }
	    }

	    return resultWitness;
	}

	public ArrayList<Case> findCasesWithMoreThanThreeTestimoniesFromSameGender(Gender gender) {
		ArrayList<Case> result = new ArrayList<>();
		ArrayList<Case> checkedCases = new ArrayList<>();

	    for (Case courtCase : allCases.values()) { 
	    	if (checkedCases.contains(courtCase)) continue;

	        int count = 0;
	        for (Witness witness : allWitnesses.values()) { 
	            if (witness.getGender() == gender) {
	                for (Testimony testimony : witness.getTestimoniesList()) {
	                    if (testimony.getCasee() != null && testimony.getCasee().equals(courtCase)) {
	                        count++;
	                    }
	                }
	            }
	        }

	        if (count > 3) {
	            result.add(courtCase);
	        }
	        checkedCases.add(courtCase);
	    }

	    return result;
	}


	// Getters  
		public Map<Integer, Lawyer> getAllLawyers() {
			return Collections.unmodifiableMap(allLawyers) ;
		}

		public Map<Integer, Accused> getAllAccuseds() {
			return Collections.unmodifiableMap(allAccuseds) ;

		}
		public Map<Integer, Employee> getAllEmployees() {
			return Collections.unmodifiableMap(allEmployees) ;

		}
		public Map<Integer, Witness> getAllWitnesses() {
			return Collections.unmodifiableMap(allWitnesses) ;

		}
		public Map<Integer, Testimony> getAllTestimonies() {
			return Collections.unmodifiableMap(allTestimonies) ;

		}
		public Map<Integer, Department> getAllDepartments() {
			return Collections.unmodifiableMap(alldepartments) ;

		}
		public Map<Integer, Courtroom> getAllCourtrooms() {
			return Collections.unmodifiableMap(allCourtrooms) ;

		}
		public Map<String, Case> getAllCases() {
			return Collections.unmodifiableMap(allCases) ;

			
		}
		public Map<Integer, Meeting> getAllMeetings() {
			return Collections.unmodifiableMap(allMeetings) ;

		}

		public Map<Integer, Verdict> getAllVerdicts() {
			return Collections.unmodifiableMap(allVerdicts) ;

		}
		public Map<Integer, Appeal> getAllAppeals() {
			return Collections.unmodifiableMap(allAppeals) ;

		}
		public Map<Integer, Document> getAllDocuments() {
			return Collections.unmodifiableMap(allDocuments) ;

		}
	
	// hw2 queries
	
	public HashMap<Employee, Stack<Department>> employeesThatWorksInMoreThanOneDepartment() {
		HashMap<Employee, Stack<Department>> toRet = new HashMap<Employee, Stack<Department>>();
		for(Employee e : allEmployees.values()) {
			if(e.getDepartments().size()>1) {
				Stack<Department> st1 = new Stack<Department>();
				st1.addAll(e.getDepartments());
				toRet.put(e, st1);
			}
		}
		return toRet;	
	}
	
	public HashMap<Department, Integer> findInActiveCasesCountByDepartment  (){
		 HashMap<Department, Integer> toRet = new  HashMap<Department, Integer>();
		 HashSet<Case> helper ;
		 for(Department d : alldepartments.values()) {
			 helper = new HashSet<Case>();
			 for(Lawyer l:d.getLawyers()) {
				 if(!(l instanceof Judge) && l.getDepartment()!=null) {

					 for(Case c: l.getCasesHandled()) {
						 if(!helper.contains(c)&& (c.getCaseStatus()== Status.finished || c.getCaseStatus()== Status.canceled)) {
				
							 helper.add(c);
					 }}
				 }
			 }
			 toRet.put(d, helper.size());
		 }
		 return toRet;
	}
	
	public Lawyer findTheSuitableLawyer22(Case casee) {
		Lawyer minCases = null;
		for(Lawyer law : allLawyers.values()) {
			if(minCases==null) minCases=law;
			if(law.getSpecialization()==casee.getCaseType()) {
				if(law.getCasesHandled().size()<5) {
					minCases=law;
					break;
				}
			}
			else {
				if(law.getCasesHandled().size()<minCases.getCasesHandled().size()) minCases=law;
			}			
		}
		Date lastestDate = null;
		for(Case c : minCases.getCasesHandled()) {
			if(lastestDate==null) lastestDate = c.getOpenedDate();
			for(Meeting m : c.getMeetingsList())
				if(m.getMeetingDate().after(lastestDate)) lastestDate = m.getMeetingDate();
		}
//		latestDay = lastestDate +2;
		Courtroom founded =null, founded2=null;
		for(Courtroom cr : minCases.getDepartment().getCourtrooms()) {
			for(Meeting m : allMeetings.values()) {
				if(m.getCourtroom().equals(cr)) {
					if(!m.getMeetingDate().equals(lastestDate)) {
						founded=cr;
						break;
					}
				}
				else {
					if(!m.getMeetingDate().equals(lastestDate)) {
						founded2=cr;
					}
					
				}
			}
			
		}
		//private Date meetingDate;
		///private Time hour;
	//	private Courtroom courtroom;
	//	private Case casee;
		Meeting newMeet;
		if(founded!=null)
			newMeet = new Meeting(lastestDate, null, founded, casee);
		else newMeet = new Meeting(lastestDate, null, founded2, casee);
		
		casee.addMeeting(newMeet);
		casee.setLawyer(minCases);
		allCases.remove(casee.getCode());
		allCases.put(casee.getCode(), casee);
		allLawyers.get(minCases.getId()).addCase(casee);
		
		return minCases;
		
	}
	public Lawyer findTheSuitableLawyer(Case casee) {
	    if (casee == null) return null;

	    Lawyer suitableLawyer = null;
	    for (Lawyer law : allLawyers.values()) {
	        if (law.getSpecialization().equals(casee.getCaseType()) && law.getCasesHandled().size() < 5) {
	            suitableLawyer = law;
	            break;
	        }
	    }

	    if (suitableLawyer == null) {
	        for (Lawyer law : allLawyers.values()) {
	            if (suitableLawyer == null || law.getCasesHandled().size() < suitableLawyer.getCasesHandled().size()) {
	                suitableLawyer = law;
	            }
	        }
	    }
	    Date latestDate = null;
	    for (Case c : suitableLawyer.getCasesHandled()) {
	        if (latestDate == null || (c.getOpenedDate() != null && c.getOpenedDate().after(latestDate))) {
	            latestDate = c.getOpenedDate();
	        }
	        for (Meeting m : c.getMeetingsList()) {
	            if (m.getMeetingDate() != null && m.getMeetingDate().after(latestDate)) {
	                latestDate = m.getMeetingDate();
	            }
	        }
	    }

	    if (latestDate == null) {
	        latestDate = new Date(); 
	    }

	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(latestDate);
	    calendar.add(Calendar.DAY_OF_MONTH, 2);
	    Date newMeetingDate = calendar.getTime();
	    Courtroom availableCourtroom = null;
	    if (suitableLawyer.getDepartment() != null) {
	        for (Courtroom cr : suitableLawyer.getDepartment().getCourtrooms()) {
	            boolean isAvailable = true;
	            for (Meeting m : allMeetings.values()) {
	                if (m.getCourtroom().equals(cr) && m.getMeetingDate().equals(newMeetingDate)) {
	                    isAvailable = false;
	                    break;
	                }
	            }
	            if (isAvailable) {
	                availableCourtroom = cr;
	                break;
	            }
	        }
	    }
	    if (availableCourtroom == null) {
	        for (Courtroom cr : allCourtrooms.values()) {
	            boolean isAvailable = true;
	            for (Meeting m : allMeetings.values()) {
	                if (m.getCourtroom().equals(cr) && m.getMeetingDate().equals(newMeetingDate)) {
	                    isAvailable = false;
	                    break;
	                }
	            }
	            if (isAvailable) {
	                availableCourtroom = cr;
	                break;
	            }
	        }
	    }
	    Time meetingTime = Time.valueOf("09:00:00");
	    Meeting newMeeting = new Meeting(newMeetingDate, meetingTime, availableCourtroom, casee);
	    casee.addMeeting(newMeeting);
	    casee.setLawyer(suitableLawyer);
	    allCases.put(casee.getCode(), casee);
	    suitableLawyer.addCase(casee);
	    return suitableLawyer;
	}

	public Employee AppointANewManager(Department department) {
		Employee newManager = department.getManager();
		for(Employee e : department.getEmployees()) {
			if(newManager!=null&&e!=newManager&& e.getWorkStartDate().before(newManager.getWorkStartDate()))
				newManager = e;
		}
		allEmployees.get(newManager.getId()).setPosition(Position.Manager);
		allEmployees.get(newManager.getId()).setSalary(newManager.getSalary()+5000);
		allEmployees.remove(department.getManager().getId());
		department.setManager(allEmployees.get(newManager.getId()));
		return allEmployees.get(newManager.getId());
	}
	
	public HashMap<Witness, HashMap<String, Queue<Case>>> getWitnessesByTypesByCases(){
		HashMap<Witness, HashMap<String, Queue<Case>>> toRet = new HashMap<Witness, HashMap<String, Queue<Case>>>();
		for(Witness wit : allWitnesses.values()) {
			toRet.put(wit, new HashMap<String, Queue<Case>>());
			toRet.get(wit).put("Criminal Case", new LinkedList<Case>());
			toRet.get(wit).put("Financial Case", new LinkedList<Case>());
			toRet.get(wit).put("Family Case", new LinkedList<Case>());
			for(Testimony t : wit.getTestimoniesList()) {
				if(t.getCasee().getVerdict()!=null&&t.getCasee().getVerdict().getAppeal()==null) {
					if(t.getCasee() instanceof CriminalCase)
						toRet.get(wit).get("Criminal Case").add(t.getCasee());
					else if(t.getCasee() instanceof FinancialCase)
						toRet.get(wit).get("Financial Case").add(t.getCasee());
					else toRet.get(wit).get("Family Case").add(t.getCasee());

				}
			}
		}
		return toRet;
	}
	

	public HashMap<String, HashMap<Courtroom, ArrayList<Meeting>>> getMeetingsByTypesByCourtroom(){
		HashMap<String, HashMap<Courtroom, ArrayList<Meeting>>> toRet = new HashMap<String, HashMap<Courtroom, ArrayList<Meeting>>>();
		toRet.put("Criminal Case", new HashMap<Courtroom, ArrayList<Meeting>>());
		toRet.put("Financial Case", new HashMap<Courtroom, ArrayList<Meeting>>());
		toRet.put("Family Case", new HashMap<Courtroom, ArrayList<Meeting>>());
		for(Meeting meet : allMeetings.values()) {
			for(Courtroom cr : allCourtrooms.values()) {
				if(meet.getCasee() instanceof CriminalCase) {
					if(!toRet.get("Criminal Case").containsKey(cr))
						toRet.get("Criminal Case").put(cr, new ArrayList<Meeting>());
					if(meet.getCourtroom().equals(cr))   toRet.get("Criminal Case").get(cr).add(meet);
				}
				else if(meet.getCasee() instanceof FinancialCase) {
					if(!toRet.get("Financial Case").containsKey(cr))
						toRet.get("Financial Case").put(cr, new ArrayList<Meeting>());
					if(meet.getCourtroom().equals(cr)) toRet.get("Financial Case").get(cr).add(meet);
				}
				else {
					if(!toRet.get("Family Case").containsKey(cr))
						toRet.get("Family Case").put(cr, new ArrayList<Meeting>());
					if(meet.getCourtroom().equals(cr)) toRet.get("Family Case").get(cr).add(meet);
					}

				}
				
			}
		return toRet;
	
		}	  
	  

	public Set<String> findUniqueCrimeToolsByCrimeScene(String crimeScene) {
	    TreeSet<String> uniqueCrimeTools = new TreeSet<>();	
	    for (Case c : allCases.values()) {
	        if (c instanceof CriminalCase criminalCase) {
	            if (criminalCase.getCrimeScene().equals(crimeScene)) {
	                uniqueCrimeTools.add(criminalCase.getCrimeTool());
	            }
	        }
	    }
	    return uniqueCrimeTools;
	}

	
	public List<FamilyCase> findFamilyCasesWithWitnessesFromBothSides(){
		ArrayList<FamilyCase> toRet =new ArrayList<FamilyCase>();
		 for(Case c : allCases.values()) {
		    	if(c instanceof FamilyCase) {
		    		for(Testimony tes : c.gettestimoniesList()) {
		    			Witness wit=tes.getWitness();
			    			if(wit.getId()== ((FamilyCase)c).getVictim().getId() && !toRet.contains(c))
			    				if(!toRet.contains((FamilyCase)c))  toRet.add((FamilyCase)c);
			    		}
			    		
		    		}
		    		
		    	}
		 return toRet;
	}
}
