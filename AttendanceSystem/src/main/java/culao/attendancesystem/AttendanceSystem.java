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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.BorderLayout; // If not already imported
import java.awt.Font; // If not already imported
import java.awt.Color; // If not already imported
import java.util.Map;
import org.jfree.chart.plot.PiePlot; // Make sure this import is present


/**
 *
 * @author nkrumah
 */
public class AttendanceSystem extends JFrame {
    private AttendanceManager manager;
    private JComboBox<Semester> semesterComboBox;
    private JComboBox<Course> courseComboBox;
    private JTextField searchField;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> weekComboBox;
    private Semester selectedSemester;
    private Course selectedCourse;
    private JLabel statusLabel;
    private JButton saveButton;
    private JButton markPresentButton;
    private JButton markAbsentButton;
    private int nextAvailableWeek = -1;
    private JLabel presentCountLabel; 
    private JLabel absentCountLabel;  
    private ChartPanel chartPanel;    
    private JFreeChart attendanceChart; 
    private int currentWeekForStats = 1; 
    private JLabel notMarkedCountLabel; 
    private JPanel weekPanel;
    private JPanel statsPanel;
    

    public AttendanceSystem() {
        // 1. Initialize the manager (this will attempt to load data)
        manager = new AttendanceManager();

        // 2. Initialize the GUI components (this creates semesterComboBox)
        initializeGUI();

        // 3. Check if data was loaded BEFORE adding sample data
        // Check if the manager actually has data loaded from the file
        if (manager.getAllSemesters().isEmpty()) {
            System.out.println("No data loaded from file, setting up sample data.");
            setupSampleData(); // Only add sample data if the file was empty/not found
        } else {
            System.out.println("Data loaded from file, skipping sample data setup and refreshing UI.");
            // Data was loaded, so skip setupSampleData which would overwrite it.
            // Instead, populate the UI combo boxes with the loaded data.
            refreshSemesterList(); // This will fill semesterComboBox with loaded Semesters

            // Optional: Auto-select the first semester to populate courseComboBox
            // and potentially load the first course's data.
            if (semesterComboBox.getItemCount() > 0) {
                // Use SwingUtilities.invokeLater to ensure the GUI is fully built
                // before trying to select an item and trigger listeners.
                SwingUtilities.invokeLater(() -> {
                    semesterComboBox.setSelectedIndex(0);
                    
                });
            }
        }
    }
    
    private void initializeGUI() {
        setTitle("Attendance Management System");
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
        
         // Control panel (rearranged to include Week selection on the far left)
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Course & Week Selection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left


        // --- Week selection (placed on the far left) ---
        gbc.gridx = 0; gbc.gridy = 0; // Start at the very beginning
        gbc.gridheight = 2; // Span two rows to align with Semester/Course vertically
        gbc.fill = GridBagConstraints.VERTICAL; // Fill vertically to match height
        JPanel weekSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Compact sub-panel
        weekSubPanel.setBackground(new Color(245, 245, 245));
        weekSubPanel.add(new JLabel("Week:"));
        weekComboBox = new JComboBox<>(); // Initialize the class field
        for (int i = 1; i <= 12; i++) {
            weekComboBox.addItem(i);
        }
        weekComboBox.setPreferredSize(new Dimension(80, 25));

        weekSubPanel.add(weekComboBox);
        controlPanel.add(weekSubPanel, gbc);
        gbc.fill = GridBagConstraints.NONE; // Reset fill for other components
        gbc.gridheight = 1; // Reset gridheight for other components
        

        // Add some horizontal spacing between Week and Semester/Course
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridheight = 2;
        controlPanel.add(Box.createHorizontalStrut(10), gbc); // Spacer
        gbc.gridheight = 1;

        // Semester selection
        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 3;
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(200, 25));
        semesterComboBox.addActionListener(e -> loadSemesterCourses());
        controlPanel.add(semesterComboBox, gbc);

        gbc.gridx = 4;
        JButton addSemesterButton = new JButton("➕ Add Semester");
        addSemesterButton.setBackground(new Color(34, 139, 34));
        addSemesterButton.setForeground(Color.WHITE);
        addSemesterButton.addActionListener(e -> showAddSemesterDialog());
        controlPanel.add(addSemesterButton, gbc);
        
        //Delete Semester Button ---
        gbc.gridx = 8;
        JButton deleteSemesterButton = new JButton("🗑️ Delete Semester");
        deleteSemesterButton.setBackground(new Color(220, 20, 60)); 
        deleteSemesterButton.setForeground(Color.WHITE);
        deleteSemesterButton.addActionListener(e -> deleteSelectedSemester());
        controlPanel.add(deleteSemesterButton, gbc);

        // Course selection
        gbc.gridx = 2; gbc.gridy = 1;
        controlPanel.add(new JLabel("Course:"), gbc);

        //gbc.gridx = 1;
        gbc.gridx = 3;
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 25));
        courseComboBox.addActionListener(e -> loadCourseData());
        controlPanel.add(courseComboBox, gbc);

        //gbc.gridx = 2;
        gbc.gridx = 4;
        JButton addCourseButton = new JButton("➕ Add Course");
        addCourseButton.setBackground(new Color(30, 144, 255));
        addCourseButton.setForeground(Color.WHITE);
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        controlPanel.add(addCourseButton, gbc);
        
        //Delete Course Button ---
        gbc.gridx = 8; 
        JButton deleteCourseButton = new JButton("🗑️ Delete Course");
        deleteCourseButton.setBackground(new Color(220, 20, 60)); 
        deleteCourseButton.setForeground(Color.WHITE);
        deleteCourseButton.addActionListener(e -> deleteSelectedCourse());
        controlPanel.add(deleteCourseButton, gbc);

        add(controlPanel, BorderLayout.NORTH);

        // Week selection and controls panel
        JPanel weekPanel = new JPanel(new BorderLayout()); 
        weekPanel.setBackground(new Color(245, 245, 245));
        weekPanel.setBorder(BorderFactory.createTitledBorder("📅 Attendance Controls"));

        //Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(new JLabel("Index Number:"));
        searchField = new JTextField(12);
        searchField.addActionListener(e -> performSearch()); 
        searchPanel.add(searchField);

        JButton searchButton = new JButton("🔍 Search");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);

        JButton clearSearchButton = new JButton("🧹 Clear");
        clearSearchButton.setBackground(new Color(169, 169, 169));
        clearSearchButton.setForeground(Color.WHITE);
        clearSearchButton.addActionListener(e -> clearSearch());
        searchPanel.add(clearSearchButton);

        weekPanel.add(searchPanel, BorderLayout.CENTER);

        // Bottom part: Attendance action buttons
        JPanel attendanceButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        attendanceButtonsPanel.setBackground(new Color(245, 245, 245));

        // Initialize and add Mark Present button
        markPresentButton = new JButton("✅ Mark Present");
        markPresentButton.setBackground(new Color(0, 100, 0));
        markPresentButton.setForeground(Color.WHITE);
        markPresentButton.addActionListener(e -> markSelectedAttendance(true));
        markPresentButton.setPreferredSize(new Dimension(120, 100));
        attendanceButtonsPanel.add(markPresentButton);

        // Initialize and add Mark Absent button
        markAbsentButton = new JButton("❌ Mark Absent");
        markAbsentButton.setBackground(new Color(220, 20, 60));
        markAbsentButton.setForeground(Color.WHITE);
        markAbsentButton.addActionListener(e -> markSelectedAttendance(false));
        markAbsentButton.setPreferredSize(new Dimension(120, 100));
        attendanceButtonsPanel.add(markAbsentButton);

        // Clear Selection button
        JButton clearButton = new JButton("🧹 Clear Selection");
        clearButton.setBackground(new Color(255, 165, 0));
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearSelection());
        clearButton.setPreferredSize(new Dimension(120, 100));
        attendanceButtonsPanel.add(clearButton);

        weekPanel.add(attendanceButtonsPanel, BorderLayout.NORTH);

        add(weekPanel, BorderLayout.WEST); 
        
        // Statistics Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Weekly Statistics"));
        
        // Labels for counts
        presentCountLabel = new JLabel("Present: 0");
        presentCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        presentCountLabel.setForeground(new Color(0, 100, 0));
        presentCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        absentCountLabel = new JLabel("Absent: 0");
        absentCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        absentCountLabel.setForeground(Color.RED); // Dark red
        absentCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        notMarkedCountLabel = new JLabel("Not Marked: 0");
        notMarkedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notMarkedCountLabel.setForeground(Color.BLUE);
        notMarkedCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(presentCountLabel);
        statsPanel.add(absentCountLabel);
        statsPanel.add(notMarkedCountLabel);
        
        // Separator before chart
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        statsPanel.add(Box.createVerticalStrut(5));

        // Initialize the chart with empty data
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Present", 0);
        dataset.setValue("Absent", 0);
        dataset.setValue("Not Marked", 0);
        attendanceChart = ChartFactory.createPieChart(
            "Week " + currentWeekForStats + " Attendance", // Chart title
            dataset,           // Dataset
            true,              // Include legend
            true,              // Tooltips
            false              // URLs
        );
        
        chartPanel = new ChartPanel(attendanceChart);
        chartPanel.setPreferredSize(new Dimension(280, 180)); // Adjust size as needed
        chartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel.add(chartPanel);
        statsPanel.add(Box.createVerticalGlue());

        weekPanel.add(statsPanel, BorderLayout.PAGE_END); // Place stats panel at the bottom

        add(weekPanel, BorderLayout.WEST); // Keep it on the WEST side
        
         // Attendance table setup
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
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Attendance Records"));
        add(scrollPane, BorderLayout.CENTER); // Center the attendance table

        
        JPanel actionPanel; // Declaration first
        actionPanel = new JPanel(); // Basic initialization
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS)); // Set layout separately
        actionPanel.setBackground(new Color(245, 245, 245));
        actionPanel.setBorder(BorderFactory.createTitledBorder("💾 Actions"));
        actionPanel.setPreferredSize(new Dimension(180, 100)); 
        
        // First row of buttons
        JPanel row1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row1Panel.setBackground(new Color(245, 245, 245));
        
        saveButton = new JButton("💾 Save Attendance");
        saveButton.setBackground(new Color(0, 100, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveAttendance());
        saveButton.setPreferredSize(new Dimension(120, 100));
        gbc.gridx = 0; gbc.gridy = 0;
        actionPanel.add(saveButton);

        JButton addStudentButton = new JButton("👥 Add Student");
        addStudentButton.setBackground(new Color(255, 165, 0));
        addStudentButton.setForeground(Color.WHITE);
        addStudentButton.addActionListener(e -> showAddStudentDialog());
        addStudentButton.setPreferredSize(new Dimension(120, 100));
        gbc.gridx = 3; gbc.gridy = 0;
        actionPanel.add(addStudentButton);
        
        JButton exportButton = new JButton("📊 Export to Excel");
        exportButton.setBackground(new Color(70, 130, 180));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportToExcel());
        exportButton.setPreferredSize(new Dimension(120, 100));
        gbc.gridx = 1; gbc.gridy = 0;
        actionPanel.add(exportButton);

        // Second row of buttons
        JPanel row2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row2Panel.setBackground(new Color(245, 245, 245));

        JButton importButton = new JButton("📥 Import Students");
        importButton.setBackground(new Color(138, 43, 226));
        importButton.setForeground(Color.WHITE);
        importButton.addActionListener(e -> showImportStudentsDialog());
        importButton.setPreferredSize(new Dimension(120, 100));
        gbc.gridx = 0; gbc.gridy = 1;
        actionPanel.add(importButton);

        JButton deleteButton = new JButton("🗑️ Delete Students");
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> showDeleteStudentDialog());
        deleteButton.setPreferredSize(new Dimension(120, 100));
        gbc.gridx = 1; gbc.gridy = 1;
        actionPanel.add(deleteButton);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(169, 169, 169));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadCourseData());
        refreshButton.setPreferredSize(new Dimension(130, 100));
        gbc.gridx = 2; gbc.gridy = 1;
        actionPanel.add(refreshButton);
        
        // Add the rows to the main action panel
        actionPanel.add(Box.createVerticalStrut(5)); // Small gap at the top
        actionPanel.add(row1Panel);
        actionPanel.add(Box.createVerticalStrut(10)); // Gap between rows
        actionPanel.add(row2Panel);
        actionPanel.add(Box.createVerticalStrut(5)); // Small gap at the bottom

        add(actionPanel, BorderLayout.EAST);

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
        Semester semester1 = new Semester("2024/2025", "Semester 1");
        Semester semester2 = new Semester("2023/2024", "Semester 2");

        // Sample courses with levels
        Course course1 = new Course("CS101", "Introduction to Computer Science", 100);
        course1.addStudent(new Student("John Doe", "CS001"));
        course1.addStudent(new Student("Jonathan Nkrumah", "CS002"));

        Course course2 = new Course("MATH201", "Calculus II", 200);
        course2.addStudent(new Student("Jonathan Nkrumah", "MATH001"));
        course2.addStudent(new Student("Charlie Wilson", "MATH002"));

        semester1.addCourse(course1);
        semester2.addCourse(course2);

        manager.addSemester(semester1);
        manager.addSemester(semester2);

        refreshSemesterList();
    }

    private void refreshSemesterList() {
        System.out.println("=== REFRESHING SEMESTER LIST ===");
        semesterComboBox.removeAllItems();
        System.out.println("Manager has " + manager.getAllSemesters().size() + " semesters.");
        for (Semester semester : manager.getAllSemesters()) {
            System.out.println("  Adding Semester to ComboBox: " + semester.getSemesterCode() + " - " + semester.getSemesterName());
            semesterComboBox.addItem(semester);
        }
        System.out.println("=== END REFRESH SEMESTER LIST ===");
    }

    private void loadSemesterCourses() {
        System.out.println("=== LOADING SEMESTER COURSES ===");
        selectedSemester = (Semester) semesterComboBox.getSelectedItem();
        System.out.println("Selected Semester from ComboBox: " + (selectedSemester != null ? selectedSemester.getSemesterCode() : "NULL"));
        courseComboBox.removeAllItems();
        if (selectedSemester != null) {
            System.out.println("Selected Semester has " + selectedSemester.getCourses().size() + " courses.");
            for (Course course : selectedSemester.getCourses()) {
                System.out.println("  Adding Course to ComboBox: " + course.getCourseCode() + " - " + course.getCourseName());
                // Let's also check the course's internal state right here
                System.out.println("    Course " + course.getCourseCode() + " Attendance Map Size: " + (course.getAttendance() != null ? course.getAttendance().size() : "NULL"));
                System.out.println("    Course " + course.getCourseCode() + " Locked Weeks: " + course.getLockedWeeks());
                courseComboBox.addItem(course);
            }
        } else {
            System.out.println("No semester selected, clearing course combo box.");
        }
        statusLabel.setText("Semester loaded: " + (selectedSemester != null ? selectedSemester.getSemesterName() : "None"));
        System.out.println("=== END LOADING SEMESTER COURSES ===");
    }

        private void loadCourseData() {
    selectedCourse = (Course) courseComboBox.getSelectedItem();
    Semester uiSelectedSemester = (Semester) semesterComboBox.getSelectedItem();
    Course uiSelectedCourse = (Course) courseComboBox.getSelectedItem();
    
    
    if (uiSelectedSemester == null || uiSelectedCourse == null) {
        tableModel.setRowCount(0);
        statusLabel.setText("No course selected");
        // --- NEW: Clear statistics when no course ---
            updateStatistics(1);
        
        markPresentButton.setEnabled(false); // Disable marking buttons
        markAbsentButton.setEnabled(false);
        return;
    }
    
    String semesterCode = uiSelectedSemester.getSemesterCode();
    String courseCode = uiSelectedCourse.getCourseCode();

    Semester canonicalSemester = manager.getSemester(semesterCode); // Need this method in AttendanceManager
    Course canonicalCourse = null;
    if (canonicalSemester != null) {
        // Assuming Semester has a method like getCourse(String code)
        canonicalCourse = canonicalSemester.getCourse(courseCode); // Need this method in Semester
    }
    
    selectedCourse = canonicalCourse != null ? canonicalCourse : uiSelectedCourse;

    if (selectedCourse == null) { // Double-check
         tableModel.setRowCount(0);
         statusLabel.setText("Course data not found.");
         return;
    }

    
    System.out.println("Loading course data for: " + selectedCourse.getCourseCode());
    System.out.println("DEBUG: Raw loaded attendance data for " + selectedCourse.getCourseCode() + ":");
        Map<String, Map<Integer, Boolean>> loadedAttendance = selectedCourse.getAttendance();
        if (loadedAttendance != null) {
            for (Map.Entry<String, Map<Integer, Boolean>> studentEntry : loadedAttendance.entrySet()) {
                System.out.println("  Student " + studentEntry.getKey() + ": " + studentEntry.getValue());
            }
        } else {
            System.out.println("  Attendance map is NULL!");
        }
        
        // Print loaded locked weeks ---
        System.out.println("DEBUG: Locked weeks for " + selectedCourse.getCourseCode() + ": " + selectedCourse.getLockedWeeks());
       
    
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

     // Update statistics after loading course data ---
        // Use the currently selected week in the ComboBox for stats
        Object selectedWeekObj = weekComboBox.getSelectedItem();
        int weekForStats = 1; // Default
        if (selectedWeekObj instanceof Integer) {
            weekForStats = (Integer) selectedWeekObj;
        }
        currentWeekForStats = weekForStats; // Sync internal tracker
        updateStatistics(currentWeekForStats);
        
    // Enable marking buttons when a course is loaded
    markPresentButton.setEnabled(true);
    markAbsentButton.setEnabled(true);
}

    // Replace the current markSelectedAttendance method with this:
private void markSelectedAttendance(boolean present) {
    int[] selectedRows = attendanceTable.getSelectedRows();
    int selectedWeek;
   
    
   // Validate and get the selected week ---
    try {
        selectedWeek = (Integer) weekComboBox.getSelectedItem(); // Assign only once
        if (selectedWeek < 1 || selectedWeek > 12) {
             JOptionPane.showMessageDialog(this, "Please select a valid week (1-12).", "Invalid Week", JOptionPane.WARNING_MESSAGE);
             return;
        }
    } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "Please select a valid week (12).", "Invalid Week", JOptionPane.WARNING_MESSAGE);
         return;
    }

    if (selectedCourse == null) {
        JOptionPane.showMessageDialog(this, "Please select a course first!", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Check if the week is locked before marking ---
        System.out.println("Checking lock status for Week " + selectedWeek + " before marking...");
        if (selectedCourse.isWeekLocked(selectedWeek)) {
            JOptionPane.showMessageDialog(this, "Week " + selectedWeek + " is locked. You cannot modify its attendance.", "Week Locked", JOptionPane.WARNING_MESSAGE);
            System.out.println("ABORTING: Week " + selectedWeek + " is locked.");
            return; // Stop the method execution
        }
        System.out.println("Week " + selectedWeek + " is NOT locked. Proceeding with marking.");

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

    // Update statistics for the week that was just marked ---
    updateStatistics(selectedWeek);
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
    
    private void updateWeekSelector() {
    weekComboBox.removeAllItems();

    if (selectedCourse != null) {
        // Populate the weekComboBox with all weeks (1-12)
        for (int i = 1; i <= 12; i++) {
            weekComboBox.addItem(i);
        }

        // Enable marking buttons when a course is selected
        markPresentButton.setEnabled(true);
        markAbsentButton.setEnabled(true);
    } else {
        // Disable marking buttons if no course is selected
        markPresentButton.setEnabled(false);
        markAbsentButton.setEnabled(false);
    }
}
    
        private void performSearch() {
        String indexNumber = searchField.getText().trim();
        if (!indexNumber.isEmpty()) {
            // Clear any existing selection
            attendanceTable.clearSelection();
            
            boolean found = false;
            // Search for rows matching the index number
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String tableIndex = (String) tableModel.getValueAt(row, 2); // Column 2 is Index Number
                if (tableIndex != null && tableIndex.equalsIgnoreCase(indexNumber)) {
                    // Select the matching row
                    attendanceTable.addRowSelectionInterval(row, row);
                    attendanceTable.scrollRectToVisible(attendanceTable.getCellRect(row, 0, true));
                    found = true;
                  
                    break;
                }
            }
            
            if (found) {
                statusLabel.setText("Found student with index: " + indexNumber);
            } else {
                statusLabel.setText("Student not found");
                JOptionPane.showMessageDialog(this, "Student with index number '" + indexNumber + "' not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            statusLabel.setText("Please enter an index number to search");
        }
    }

    private void clearSearch() {
        searchField.setText("");
        attendanceTable.clearSelection();
        statusLabel.setText("Search cleared");
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
            
            int weekJustSaved = (Integer) weekComboBox.getSelectedItem();
            System.out.println("Attempting to lock Week " + weekJustSaved + " for course " + selectedCourse.getCourseCode());
            selectedCourse.lockWeek(weekJustSaved); // Lock this week in the course data

            saveButton.setText("Saved!");
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveButton.setText("💾 Save Attendance");
                }
            });
            timer.setRepeats(false);
            timer.start();
            statusLabel.setText("Attendance data saved successfully!");
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
    
    
    private void updateStatistics(int week) {
        
        // --- NEW: Update the stats panel title ---
    if (statsPanel != null) {
        // Get the current border
        javax.swing.border.Border currentBorder = statsPanel.getBorder();
        // Check if it's a TitledBorder
        if (currentBorder instanceof TitledBorder) {
            // Cast it and update the title
            TitledBorder titledBorder = (TitledBorder) currentBorder;
            titledBorder.setTitle("📊 Week " + week + " Statistics");
            // Repaint the panel to reflect the border change
            statsPanel.repaint();
        } else {
                    
        }
    }
        if (selectedCourse == null || week < 1 || week > 12) {
            presentCountLabel.setText("Present: 0 (0.00%)");
            absentCountLabel.setText("Absent: 0 (0.00%)");
            notMarkedCountLabel.setText("Not Marked: 0 (0.00%)");
            updateChart(0, 0, 0, week); // Update chart with zero values
            return;
        }

        int presentCount = 0;
        int absentCount = 0;
        int notMarkedCount = 0; 
        java.util.List<Student> students = selectedCourse.getStudents();

        for (Student student : students) {
            Boolean isPresent = selectedCourse.getAttendance(student.getIndexNumber(), week);
            if (Boolean.TRUE.equals(isPresent)) {
                presentCount++;
            } else if (Boolean.FALSE.equals(isPresent)) {
                absentCount++;
            } else { 
                notMarkedCount++;
            }
        }
        int totalCount = presentCount + absentCount + notMarkedCount;
        
        // Calculate percentages, handling division by zero
        double presentPercentage = (totalCount > 0) ? (presentCount * 100.0 / totalCount) : 0.0;
        double absentPercentage = (totalCount > 0) ? (absentCount * 100.0 / totalCount) : 0.0;
        double notMarkedPercentage = (totalCount > 0) ? (notMarkedCount * 100.0 / totalCount) : 0.0;

        // Update the labels with counts and percentages
        // Using String.format for cleaner percentage formatting
        presentCountLabel.setText(String.format("Present: %d (%.2f%%)", presentCount, presentPercentage));
        absentCountLabel.setText(String.format("Absent: %d (%.2f%%)", absentCount, absentPercentage));
        notMarkedCountLabel.setText(String.format("Not Marked: %d (%.2f%%)", notMarkedCount, notMarkedPercentage));

        // Update the chart
        updateChart(presentCount, absentCount, notMarkedCount, week);

        // Update the chart title
        if (attendanceChart != null) {
             attendanceChart.setTitle("Week " + week + " Attendance");
        }

        System.out.println("Updated statistics for Week " + week + ": P=" + presentCount + ", A=" + absentCount + ", NM=" + notMarkedCount);
    }
    
    private void updateChart(int present, int absent, int notMarked, int week) {
    DefaultPieDataset dataset = new DefaultPieDataset();
    
    // Create the EXACT string labels that will be used as keys
    String presentLabel = "Present (" + present + ")";
    String absentLabel = "Absent (" + absent + ")";
    String notMarkedLabel = "Not Marked (" + notMarked + ")";
    
    // Add data using these string labels as keys
    dataset.setValue(presentLabel, present);
    dataset.setValue(absentLabel, absent);
    dataset.setValue(notMarkedLabel, notMarked);

    // Update the existing chart's dataset
    if (attendanceChart != null && attendanceChart.getPlot() instanceof PiePlot) {
        PiePlot plot = (PiePlot) attendanceChart.getPlot();
        plot.setDataset(dataset);
        plot.setSectionPaint(presentLabel, Color.GREEN);
        plot.setSectionPaint(absentLabel, Color.RED);
        plot.setSectionPaint(notMarkedLabel, Color.BLUE);
        
        plot.setSectionOutlinesVisible(false);
        plot.setShadowGenerator(null);
        
    } else if (attendanceChart == null) {
        // If the chart hasn't been created yet, create it with the dataset
        attendanceChart = ChartFactory.createPieChart(
            "Week " + week + " Attendance", // Chart title
            dataset,           // Dataset
            true,              // Include legend
            true,              // Tooltips
            false              // URLs
        );
        
        //Set fixed colors for the sections (for initial creation) ---
        if (attendanceChart.getPlot() instanceof PiePlot) {
            PiePlot plot = (PiePlot) attendanceChart.getPlot();
            
            plot.setSectionPaint(presentLabel, Color.GREEN);
            plot.setSectionPaint(absentLabel, Color.RED);
            plot.setSectionPaint(notMarkedLabel, Color.BLUE);
            
            plot.setSectionOutlinesVisible(false);
            plot.setShadowGenerator(null);
        }
        
        // Update the chart panel if it exists
        if (chartPanel != null) {
            chartPanel.setChart(attendanceChart);
        }
    }

    // Refresh the chart panel
    if (chartPanel != null) {
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
    
    private void deleteSelectedSemester() {
        Semester selectedSem = (Semester) semesterComboBox.getSelectedItem();
        if (selectedSem == null) {
            JOptionPane.showMessageDialog(this, "Please select a semester to delete.", "No Semester Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the semester '" + selectedSem.getSemesterName() + "' (" + selectedSem.getSemesterCode() + ")?\n" +
                "This will also delete all courses and attendance data associated with this semester.",
                "Confirm Delete Semester",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            String semesterCode = selectedSem.getSemesterCode();
            // Remove from the manager
            manager.removeSemester(semesterCode); 
            // Update the UI
            refreshSemesterList();
            // Clear course combo box as the semester is gone
            courseComboBox.removeAllItems();
            // Clear the attendance table
            tableModel.setRowCount(0);
            statusLabel.setText("Deleted semester: " + selectedSem.getSemesterName());
            // If there are semesters left, select the first one
            if (semesterComboBox.getItemCount() > 0) {
                semesterComboBox.setSelectedIndex(0);
            } else {
                // No semesters left, clear related UI elements
                selectedSemester = null;
                selectedCourse = null;
                statusLabel.setText("No semesters available.");
            }
        }
    }

    /**
     * Deletes the currently selected course after confirmation.
     */
    private void deleteSelectedCourse() {
        if (selectedSemester == null) {
            JOptionPane.showMessageDialog(this, "Please select a semester first.", "No Semester", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course selectedCrs = (Course) courseComboBox.getSelectedItem();
        if (selectedCrs == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the course '" + selectedCrs.getCourseName() + "' (" + selectedCrs.getCourseCode() + ")?\n" +
                "This will delete all attendance data associated with this course.",
                "Confirm Delete Course",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            String courseCode = selectedCrs.getCourseCode();
            // Remove from the selected semester (assuming Semester has this method)
            selectedSemester.removeCourse(courseCode); // You'll need to implement this method in Semester
            // Update the UI
            loadSemesterCourses(); 
            tableModel.setRowCount(0);
            statusLabel.setText("Deleted course: " + selectedCrs.getCourseName());
            // If there are courses left in the semester, select the first one
            if (courseComboBox.getItemCount() > 0) {
                courseComboBox.setSelectedIndex(0);
                // loadCourseData() should be triggered by the ActionListener on courseComboBox
            } else {
                // No courses left in this semester
                selectedCourse = null;
                statusLabel.setText("No courses available in this semester.");
            }
        }
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
                      
                    }
                }

                new AttendanceSystem().setVisible(true);
            }
        });
    }
}