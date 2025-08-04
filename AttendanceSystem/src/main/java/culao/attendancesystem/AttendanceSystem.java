/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package culao.attendancesystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 *
 * @author nkrumah
 */
public class AttendanceSystem extends JFrame {
    private AttendanceManager manager;
    private JComboBox<Semester> semesterComboBox;
    private JComboBox<Course> courseComboBox;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> weekComboBox;
    private Semester selectedSemester;
    private Course selectedCourse;
    private JLabel statusLabel;
    private JButton saveButton;

    public AttendanceSystem() {
        manager = new AttendanceManager();
        initializeGUI();
        setupSampleData();
    }

    private void initializeGUI() {
        setTitle("🎓 Advanced Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header panel with attractive styling
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("🎓 Attendance Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Track student attendance efficiently");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createTitledBorder("📋 Course Selection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Semester selection
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(200, 25));
        semesterComboBox.addActionListener(e -> loadSemesterCourses());
        controlPanel.add(semesterComboBox, gbc);

        gbc.gridx = 2;
        JButton addSemesterButton = new JButton("➕ Add Semester");
        addSemesterButton.setBackground(new Color(34, 139, 34));
        addSemesterButton.setForeground(Color.WHITE);
        addSemesterButton.addActionListener(e -> showAddSemesterDialog());
        controlPanel.add(addSemesterButton, gbc);

        // Course selection
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 25));
        courseComboBox.addActionListener(e -> loadCourseData());
        controlPanel.add(courseComboBox, gbc);

        gbc.gridx = 2;
        JButton addCourseButton = new JButton("➕ Add Course");
        addCourseButton.setBackground(new Color(30, 144, 255));
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        controlPanel.add(addCourseButton, gbc);

        add(controlPanel, BorderLayout.NORTH);

        // Week selection and controls
        JPanel weekPanel = new JPanel(new FlowLayout());
        weekPanel.setBackground(new Color(240, 248, 255));
        weekPanel.setBorder(BorderFactory.createTitledBorder("📅 Attendance Controls"));

        weekPanel.add(new JLabel("Week:"));
        weekComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            weekComboBox.addItem(i);
        }
        weekComboBox.setPreferredSize(new Dimension(60, 25));
        weekPanel.add(weekComboBox);

        JButton markPresentButton = new JButton("✅ Mark Present");
        markPresentButton.setBackground(new Color(50, 205, 50));
        markPresentButton.setForeground(Color.WHITE);
        markPresentButton.addActionListener(e -> markSelectedAttendance(true));
        weekPanel.add(markPresentButton);

        JButton markAbsentButton = new JButton("❌ Mark Absent");
        markAbsentButton.setBackground(new Color(220, 20, 60));
        markAbsentButton.setForeground(Color.WHITE);
        markAbsentButton.addActionListener(e -> markSelectedAttendance(false));
        weekPanel.add(markAbsentButton);

        JButton clearButton = new JButton("🧹 Clear Selection");
        clearButton.setBackground(new Color(255, 165, 0));
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearSelection());
        weekPanel.add(clearButton);

        add(weekPanel, BorderLayout.SOUTH); // Ensure it's added to the SOUTH position

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.setBorder(BorderFactory.createTitledBorder("💾 Actions"));

        saveButton = new JButton("💾 Save Attendance");
        saveButton.setBackground(new Color(0, 100, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveAttendance());
        actionPanel.add(saveButton);

        JButton exportButton = new JButton("📊 Export to Excel");
        exportButton.setBackground(new Color(70, 130, 180));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportToExcel());
        actionPanel.add(exportButton);

        JButton addStudentButton = new JButton("👥 Add Student");
        addStudentButton.setBackground(new Color(138, 43, 226));
        addStudentButton.setForeground(Color.WHITE);
        addStudentButton.addActionListener(e -> showAddStudentDialog());
        actionPanel.add(addStudentButton);

        JButton importButton = new JButton("📥 Import Students");
        importButton.setBackground(new Color(138, 43, 226));
        importButton.setForeground(Color.WHITE);
        importButton.addActionListener(e -> showImportStudentsDialog());
        actionPanel.add(importButton);

        JButton deleteButton = new JButton("🗑️ Delete Students");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> showDeleteStudentDialog());
        actionPanel.add(deleteButton);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(169, 169, 169));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadCourseData());
        actionPanel.add(refreshButton);

        add(actionPanel, BorderLayout.EAST);

        // Attendance table
        String[] columnNames = {"#", "Name", "Index Number"};
        for (int i = 1; i <= 12; i++) {
            columnNames = addElementToArray(columnNames, "Week " + i);
        }
        columnNames = addElementToArray(columnNames, "📊 %");

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 2 && column <= 14; // Only attendance columns are editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex > 2 && columnIndex <= 14) {
                    return Boolean.class;
                } else if (columnIndex == 0) {
                    return Integer.class;
                } else if (columnIndex == 15) {
                    return Double.class;
                }
                return String.class;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Attendance Records"));
        add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);
        add(statusLabel, BorderLayout.PAGE_END);

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setupSampleData() {
        // Sample semester
        Semester semester1 = new Semester("FALL2024", "Fall 2024");
        Semester semester2 = new Semester("SPRING2024", "Spring 2024");

        // Sample courses with levels
        Course course1 = new Course("CS101", "Introduction to Computer Science", 100);
        course1.addStudent(new Student("John Doe", "CS001"));
        course1.addStudent(new Student("Jane Smith", "CS002"));
        course1.addStudent(new Student("Bob Johnson", "CS003"));

        Course course2 = new Course("MATH201", "Calculus II", 200);
        course2.addStudent(new Student("Alice Brown", "MATH001"));
        course2.addStudent(new Student("Charlie Wilson", "MATH002"));

        semester1.addCourse(course1);
        semester2.addCourse(course2);

        manager.addSemester(semester1);
        manager.addSemester(semester2);

        refreshSemesterList();
    }

    private void refreshSemesterList() {
        semesterComboBox.removeAllItems();
        for (Semester semester : manager.getAllSemesters()) {
            semesterComboBox.addItem(semester);
        }
    }

    private void loadSemesterCourses() {
        selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        courseComboBox.removeAllItems();

        if (selectedSemester != null) {
            for (Course course : selectedSemester.getCourses()) {
                courseComboBox.addItem(course);
            }
        }
        statusLabel.setText("Semester loaded: " + (selectedSemester != null ? selectedSemester.getSemesterName() : "None"));
    }

    private void loadCourseData() {
        selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            tableModel.setRowCount(0);
            statusLabel.setText("No course selected");
            return;
        }
        System.out.println("Loading course data for: " + selectedCourse.getCourseCode());
        debugAttendanceData(); // Debug what we're loading

        tableModel.setRowCount(0);

        int rowNum = 1;
        for (Student student : selectedCourse.getStudents()) {
            Object[] rowData = new Object[16];
            rowData[0] = rowNum++;
            rowData[1] = student.getName();
            rowData[2] = student.getIndexNumber();

            int presentCount = 0;
            int totalWeeks = 0;

            for (int week = 1; week <= 12; week++) {
                Boolean present = selectedCourse.getAttendance(student.getIndexNumber(), week);
                rowData[week + 2] = present;
                if (present != null) {
                    totalWeeks++;
                    if (present) presentCount++;
                }
            }

            double percentage = totalWeeks > 0 ? (presentCount * 100.0 / totalWeeks) : 0.0;
            rowData[15] = percentage;

            tableModel.addRow(rowData);
        }

        statusLabel.setText("Loaded " + selectedCourse.getStudents().size() + " students for " + selectedCourse.getCourseName());
    }

    // This method was correctly positioned
    private void markSelectedAttendance(boolean present) {
        int[] selectedRows = attendanceTable.getSelectedRows();
        int selectedWeek = (Integer) weekComboBox.getSelectedItem();

        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select students first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("=== MARKING ATTENDANCE ===");
        System.out.println("Selected students: " + selectedRows.length);
        System.out.println("Selected week: " + selectedWeek);
        System.out.println("Marking as present: " + present);

        for (int row : selectedRows) {
            String indexNumber = (String) tableModel.getValueAt(row, 2);
            System.out.println("Marking student: " + indexNumber);

            // Mark in the course data model
            selectedCourse.markAttendance(indexNumber, selectedWeek, present);
            System.out.println("Course model updated for " + indexNumber);

            // Update the table UI
            tableModel.setValueAt(present, row, selectedWeek + 2);
            System.out.println("Table UI updated for " + indexNumber);

            // Verify the marking worked
            Boolean marked = selectedCourse.getAttendance(indexNumber, selectedWeek);
            System.out.println("Verification - " + indexNumber + " Week " + selectedWeek + " = " + marked);
        }

        updatePercentages();
        statusLabel.setText("Marked " + selectedRows.length + " student(s) for Week " + selectedWeek);

        // Debug the current state
        debugAttendanceData();
        System.out.println("=== END MARKING ===");
    }

    private void updatePercentages() {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int presentCount = 0;
            int totalWeeks = 0;

            for (int week = 1; week <= 12; week++) {
                Object value = tableModel.getValueAt(row, week + 2);
                if (value != null) {
                    totalWeeks++;
                    if (value instanceof Boolean && (Boolean) value) {
                        presentCount++;
                    }
                }
            }

            double percentage = totalWeeks > 0 ? (presentCount * 100.0 / totalWeeks) : 0.0;
            tableModel.setValueAt(percentage, row, 15);
        }
    }

    private void clearSelection() {
        attendanceTable.clearSelection();
        statusLabel.setText("Selection cleared");
    }

    private void debugAttendanceData() {
        if (selectedCourse != null) {
            System.out.println("=== DEBUG ATTENDANCE DATA ===");
            System.out.println("Course: " + selectedCourse.getCourseCode());
            for (Student student : selectedCourse.getStudents()) {
                String index = student.getIndexNumber();
                System.out.print("Student " + index + ": ");
                for (int week = 1; week <= 3; week++) { // Show first 3 weeks for brevity
                    Boolean att = selectedCourse.getAttendance(index, week);
                    System.out.print("W" + week + "=" + (att == null ? "null" : (att ? "P" : "A")) + " ");
                }
                System.out.println();
            }
            System.out.println("============================");
        }
    }

    private void saveAttendance() {
        if (selectedSemester != null && selectedCourse != null) {
            System.out.println("Saving attendance data...");
            debugAttendanceData(); // Debug what we're saving

            manager.saveToFile();

            saveButton.setText("✅ Saved!");
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveButton.setText("💾 Save Attendance");
                }
            });
            timer.setRepeats(false);
            timer.start();
            statusLabel.setText("Attendance data saved successfully!");

            // Reload to confirm
            loadCourseData();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a semester and course first!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportToExcel() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new java.io.File(selectedCourse.getCourseCode() + "_attendance.xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.endsWith(".xlsx")) {
                    filename += ".xlsx";
                }

                ExcelExporter.exportAttendanceToExcel(selectedCourse, filename);
                statusLabel.setText("Exported to Excel successfully!");
                JOptionPane.showMessageDialog(this, "Exported to Excel successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting to Excel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showAddSemesterDialog() {
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Semester Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Semester Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Semester",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (!code.isEmpty() && !name.isEmpty()) {
                Semester newSemester = new Semester(code, name);
                manager.addSemester(newSemester);
                refreshSemesterList();
                semesterComboBox.setSelectedItem(newSemester);
                statusLabel.setText("Added new semester: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showAddCourseDialog() {
        if (selectedSemester == null) {
            JOptionPane.showMessageDialog(this, "Please select a semester first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField levelField = new JTextField("100");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Course Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Course Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Level (e.g., 100, 200):"));
        panel.add(levelField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String levelStr = levelField.getText().trim();

            if (!code.isEmpty() && !name.isEmpty() && !levelStr.isEmpty()) {
                try {
                    int level = Integer.parseInt(levelStr);
                    Course newCourse = new Course(code, name, level);
                    selectedSemester.addCourse(newCourse);
                    loadSemesterCourses();
                    courseComboBox.setSelectedItem(newCourse);
                    statusLabel.setText("Added new course: " + name);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid level number!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showAddStudentDialog() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField indexField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Student Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Index Number:"));
        panel.add(indexField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String index = indexField.getText().trim();

            if (!name.isEmpty() && !index.isEmpty()) {
                selectedCourse.addStudent(new Student(name, index));
                loadCourseData();
                statusLabel.setText("Added new student: " + name);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showImportStudentsDialog() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Students from Excel");
        // Simple file filter without FileNameExtensionFilter
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx");
            }

            @Override
            public String getDescription() {
                return "Excel Files (*.xlsx)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    JOptionPane.showMessageDialog(this, "Please select an Excel file (.xlsx)!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<Student> students = ExcelImporter.importStudents(filePath);

                int count = 0;
                for (Student student : students) {
                    // Check if student already exists
                    boolean exists = false;
                    for (Student existingStudent : selectedCourse.getStudents()) {
                        if (existingStudent.getIndexNumber().equals(student.getIndexNumber())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        selectedCourse.addStudent(student);
                        count++;
                    }
                }

                loadCourseData();
                statusLabel.setText("Imported " + count + " new students successfully!");
                JOptionPane.showMessageDialog(this, "Imported " + count + " new students successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error importing students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showDeleteStudentDialog() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] selectedRows = attendanceTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select students to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder studentNames = new StringBuilder();
        for (int row : selectedRows) {
            String name = (String) tableModel.getValueAt(row, 1);
            studentNames.append(name).append("\n");
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the following " + selectedRows.length + " student(s)?\n" + studentNames.toString(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // Delete in reverse order to avoid index issues
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                String indexNumber = (String) tableModel.getValueAt(row, 2);
                selectedCourse.removeStudent(indexNumber);
                tableModel.removeRow(row);
            }
            statusLabel.setText("Deleted " + selectedRows.length + " student(s)");
        }
    }

    private String[] addElementToArray(String[] array, String element) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Try to set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    try {
                        // Fallback to Nimbus if available
                        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            if ("Nimbus".equals(info.getName())) {
                                UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        // Use default look and feel
                    }
                }

                new AttendanceSystem().setVisible(true);
            }
        });
    }
}