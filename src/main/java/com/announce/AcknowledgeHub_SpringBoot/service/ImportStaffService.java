package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.*;
import com.announce.AcknowledgeHub_SpringBoot.model.Role;
import com.announce.AcknowledgeHub_SpringBoot.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Service
public class ImportStaffService {

//    @Autowired
//    StaffRepository staffRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    private static final int CODE_LENGTH = 5;

    public String generateRegistrationCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt((int) Math.pow(10, CODE_LENGTH));
        String code = String.format("0%" + CODE_LENGTH + "d", number);
        return code;
    }

    //original method
    public void importExcel(MultipartFile file) throws Exception {
        //for staff table
//        List<Staff> staffs = new ArrayList<>();

        //for user table
        List<User> staffs = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellInRow = currentRow.iterator();
                //for staff table
//                Staff staff = new Staff();
                //for user table
                User staff = new User();

                int cellIndex = 0;
                String departmentName = null;
                String companyName = null;
                String positionName = null;
                while (cellInRow.hasNext()) {
                    Cell currentCell = cellInRow.next();

                    switch (cellIndex) {
                        case 0:
                            if (currentCell.getCellType() == CellType.NUMERIC){
                                staff.setStaff_id(String.valueOf((long) currentCell.getNumericCellValue()));
                            } else if (currentCell.getCellType() == CellType.STRING) {
                                staff.setStaff_id(currentCell.getStringCellValue());
                            }
                            break;
                        case 1:
                            staff.setName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            staff.setEmail(currentCell.getStringCellValue());
                            break;
//                        case 2:
////                            staff.setPh_number(currentCell.getStringCellValue());
//                            staff.setPh_number(String.valueOf((long) currentCell.getNumericCellValue()));
//                            break;
                        case 3:
                            if (currentCell.getCellType() == CellType.NUMERIC) {
                                // Convert numeric value to a string
                                staff.setPh_number(String.valueOf((long) currentCell.getNumericCellValue()));
                            } else if (currentCell.getCellType() == CellType.STRING) {
                                // If it's already a string, just set it directly
                                staff.setPh_number(currentCell.getStringCellValue());
                            }
                            break;
//                        case 3: {
//                            String roleName = getCellStringValue(currentCell).trim(); // Trim whitespace
//                            try {
//                                staff.setRole(Role.valueOf(roleName.toUpperCase()));
//                            } catch (IllegalArgumentException e) {
//                                throw new IllegalArgumentException("Invalid role: " + roleName);
//                            }
//                            break;
//                        }
//                        case 4:
//                            staff.setPosition(currentCell.getStringCellValue());
//                            break;
                        case 4:
                            positionName = getCellStringValue(currentCell);
                            break;
                        case 5:
                            departmentName = getCellStringValue(currentCell).trim();
                            break;
                        case 6:
                            companyName = getCellStringValue(currentCell).trim();
                            break;
                        case 7: {
                            String roleName = getCellStringValue(currentCell).trim(); // Trim whitespace
                            // Normalize the role name by replacing spaces with underscores
                            roleName = roleName.replace(" ", "_").toUpperCase();
                            if (!isValidRole(roleName)) {
                                roleName = addUnderscoreToRoleName(roleName);
                            }
                            try {
                                // Use the correct Role enum
                                staff.setRole(Role.valueOf(roleName));//.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                throw new IllegalArgumentException("Invalid role: " + roleName);
                            }
                            break;
                        }

//                        case 8:
//                            staff.setStatus(currentCell.getBooleanCellValue());
//                            break;
//                        case 5:
//                            staff.setDepartment_name(currentCell.getStringCellValue());
//                            break;
//                        case 6:
//                            staff.setCompany_name(currentCell.getStringCellValue());
//                            break;
//                        case 7:
//                            String groupName = getCellStringValue(currentCell).trim();
//                            Group group = getOrCreateGroup(groupName);
//                            staff.setGroup(group);
//                            break;
//                        case 5:
//                            String departmentName = getCellStringValue(currentCell).trim();
//                            Department department = getOrCreateDepartment(departmentName);
//                            staff.setDepartment(department);
//                            break;
//                        case 5:
//                            String companyName = getCellStringValue(currentCell).trim();
//                            Company company = getOrCreateCompany(companyName);
//                            staff.setDepartment.Company.Name(company);
//                            break;
//                        case 5:
//                            departmentName = getCellStringValue(currentCell).trim();
//                            break;
//                        case 6:
//                            companyName = getCellStringValue(currentCell).trim();
//                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + cellIndex);
                    }
                    cellIndex++;
                }

                staff.setStatus(true);

                //Generate and set registration code
                staff.setRegistration_code(generateRegistrationCode());

                // Ensure the department and company are created and linked
                if (departmentName != null && companyName != null) {
                    Company company = getOrCreateCompany(companyName);
                    Department department = getOrCreateDepartment(departmentName, company);
                    staff.setDepartment(department);
                    staff.setCompany(company);
                }
//                  if (departmentName != null){
//                      Department department = getOrCreateDepartment(departmentName);
//                      staff.setDepartment(department);
//                  }

                //store company
                if (companyName != null) {
                    Company company = getOrCreateCompany(companyName);
                    staff.setCompany(company);
                }

                if (positionName != null) {
                    Position position = getOrCreatePosition(positionName);
                    staff.setPosition(position);
                }


                // validate blank or space
                validateStaffRow(staff, rowNumber);

                // check email exist
//                boolean emailExist = staffRepository.findByEmail(staff.getEmail()).isPresent();
                //for user table
                boolean emailExist = userRepository.findByEmail(staff.getEmail()).isPresent();
                if (!emailExist) {
                    staffs.add(staff);
                } else {
                    System.out.println("Duplicate email found, skipping: " + staff.getEmail());
                }
//                staffs.add(staff);
            }
            //for staff table
//            staffRepository.saveAll(staffs);
            //for user table
            userRepository.saveAll(staffs);
        }
    }

    private Position getOrCreatePosition(String positionName) {
        Optional<Position> existingPosition = positionRepository.findByNameIgnoreCase(positionName);
        if (existingPosition.isPresent()) {
            return existingPosition.get();
        } else {
            throw new IllegalArgumentException("Position not found: " + positionName);
        }
    }

//    private void validateStaffRow(Staff staff, int rowNumber) {
//        if (isBlank(staff.getName()) || isBlank(staff.getEmail()) || isBlank(staff.getPh_number())
//                || staff.getRole() == null || isBlank(staff.getDepartment_name()) || isBlank(staff.getCompany_name())
//                || staff.getGroup() == null) {
//            throw new IllegalArgumentException("Row " + (rowNumber + 1) + " in your Excel sheet is blank or has spaces. Please fill it completely.");
//        }
//    }

    //for staff table
//    private void validateStaffRow(Staff staff, int rowNumber) {
//        if (isBlank(staff.getName()) || isBlank(staff.getEmail()) || isBlank(staff.getPh_number())
//                || staff.getRole() == null
//                || staff.getDepartment() == null) {
//            throw new IllegalArgumentException("Row " + (rowNumber + 1) + " in your Excel sheet is blank or has spaces. Please fill it completely.");
//        }
//    }
    //for user table
    private void validateStaffRow(User staff, int rowNumber) {
        if (isBlank(staff.getName()) || isBlank(staff.getEmail()) || isBlank(staff.getPh_number())
                || staff.getRole() == null
                || staff.getDepartment() == null) {
            throw new IllegalArgumentException("Row " + (rowNumber + 1) + " in your Excel sheet is blank or has spaces. Please fill it completely.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Company getOrCreateCompany(String companyName) {
        Optional<Company> existingCompany = companyRepository.findByName(companyName);
        if (existingCompany.isPresent()) {
            return existingCompany.get();
        } else {
            Company newCompany = new Company();
            newCompany.setName(companyName);
            return companyRepository.save(newCompany);
        }
    }

//    private Department getOrCreateDepartment(String departmentName){
//        Optional<Department> existingDepartment = departmentRepository.findByName(departmentName);
//        if (existingDepartment.isPresent()){
//           return existingDepartment.get();
//        }else {
//            Department newDepartment = new Department();
//            newDepartment.setName(departmentName);
//            return departmentRepository.save(newDepartment);
//        }
//    }

    private Department getOrCreateDepartment(String departmentName, Company company) {
        Optional<Department> existingDepartment = departmentRepository.findByNameAndCompany(departmentName, company);
        if (existingDepartment.isPresent()) {
            return existingDepartment.get();
        } else {
            Department newDepartment = new Department();
            newDepartment.setName(departmentName);
            newDepartment.setCompany(company);
            return departmentRepository.save(newDepartment);
        }
    }
//private Department getOrCreateDepartment(String departmentName) {
//    Optional<Department> existingDepartment = departmentRepository.findByNameAndCompany(departmentName);
//    if (existingDepartment.isPresent()) {
//        return existingDepartment.get();
//    } else {
//        Department newDepartment = new Department();
//        newDepartment.setName(departmentName);
//        return departmentRepository.save(newDepartment);
//    }
//}

    //create group table
    private Group getOrCreateGroup(String groupName) {
        Optional<Group> existingGroup = groupService.findByName(groupName);
        if (existingGroup.isPresent()) {
            return existingGroup.get();
        } else {
            Group newGroup = new Group();
            newGroup.setName(groupName);
            return groupRepository.save(newGroup);
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return "";
    }

    public void registerUser(User user, String departmentName, String companyName) {
        // Generate and set registration code
        user.setRegistration_code(generateRegistrationCode());
    }

    // Method to check if a role name is valid
    private boolean isValidRole(String roleName) {
        try {
            Role.valueOf(roleName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Method to insert underscores in common positions for role names
    private String addUnderscoreToRoleName(String roleName) {
        if (roleName.equals("MAINHR")) {
            return "MAIN_HR";
        } else if (roleName.equals("SUBHR")) {
            return "SUB_HR";
        }
        // Add more conditions as needed for other roles
        return roleName;
    }

}






///////////////////////////////////////////////////////////////




