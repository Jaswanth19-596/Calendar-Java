package calendarApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



public class WeekCalendarTest {
    public static void getCalendar(String username , WeekCalendar cal, ArrayList<User> userEvents) {
        JFrame frm = new JFrame();



        ImageIcon icon = new ImageIcon("../assets/calendar.png");
        frm.setTitle("Calendar");
        frm.setIconImage(icon.getImage());
        frm.setVisible(false);



        for(int i =0;i<Config.names.length;i++){
            if(Config.names[i].equals(username)){
                cal.setCurrentUser(i);
                break;
            }
        }


        cal.addCalendarEventClickListener(e -> {
            String eventDetails = getEventDetails(e);

            if(!e.getCalendarEvent().getParticipants().isEmpty()){
                                eventDetails=eventDetails+"Participants: " + String.join(", ", e.getCalendarEvent().getParticipants());
                            }

            
            JPanel eventPanel = new JPanel(new FlowLayout());
            JTextArea eventTextArea = new JTextArea(eventDetails);
            eventTextArea.setEditable(false);
            eventPanel.add(eventTextArea, BorderLayout.CENTER);
            
            
                // Find the index of the host in the userEvents list
            int hostIndex = -1;
            for (int j = 0; j < userEvents.size(); j++) {
                if (userEvents.get(j).getName().equals(e.getCalendarEvent().getHost())) {
                    hostIndex = j;
                    break;
                }
            }

            // Check if the current user is the host
            if (hostIndex != -1 && hostIndex == cal.currentUser) {
                JButton removeButton = new JButton("Remove");
                JButton editButton = new JButton("Edit");
                removeButton.addActionListener(removeEvent -> {
                    // Remove the event
                    ArrayList<String> participants = e.getCalendarEvent().getParticipants();
                    cal.removeEvent(e.getCalendarEvent(), participants);
                    JOptionPane.getRootFrame().dispose(); // Close the dialog after removal
                });

                

                editButton.addActionListener(editEvent -> {
                    editEventHandler(editEvent, frm, cal, userEvents, e);
                    JOptionPane.getRootFrame().dispose();
                });


                eventPanel.add(removeButton);
                eventPanel.add(editButton);
            }
                            
            JOptionPane.showMessageDialog(null, eventPanel, e.getCalendarEvent().getEventTitle(), JOptionPane.INFORMATION_MESSAGE);
        });
        cal.addCalendarEmptyClickListener(e -> System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30)));

        JButton goToTodayBtn = new JButton("Today");
        goToTodayBtn.addActionListener(e -> cal.goToToday());

        JButton nextWeekBtn = new JButton(">");
        nextWeekBtn.addActionListener(e -> cal.nextWeek());

        JButton prevWeekBtn = new JButton("<");
        prevWeekBtn.addActionListener(e -> cal.prevWeek());

        JButton addButton = getAddButton(cal, userEvents, frm);

        JButton logout = new JButton("logout");
        logout.addActionListener(e -> {
            new LoginPage();
            frm.setVisible(false);
        });


        JPanel weekControls = new JPanel();
        weekControls.add(prevWeekBtn);
        weekControls.add(goToTodayBtn);
        weekControls.add(nextWeekBtn);
        weekControls.add(addButton);
        weekControls.add(logout);


        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1000, 900);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private static String getEventDetails(CalendarEventClickEvent e) {
        StringBuilder sb = new StringBuilder(e.getCalendarEvent().getText());

        int i = 0;
        int wrapLength = 55 ;
        while (i + wrapLength < sb.length() && (i = sb.lastIndexOf(" ", i + wrapLength)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
                return "Event Details: "+ e.getCalendarEvent().getEventTitle() +"\n" +
                        "-------------------------------------------\n" +
                        e.getCalendarEvent().getStart() + " - " + e.getCalendarEvent().getEnd() + "\n" +
                        "Description: \n" + sb + "\n\n" +
                        "Host: " + e.getCalendarEvent().getHost() + "\n";

    }

    private static JButton getAddButton(WeekCalendar cal, ArrayList<User> userEvents, JFrame frm) {
        JButton addButton = new JButton("Add Event");
        addButton.addActionListener(e -> {
            // Create the dialog
            JDialog dialog = new JDialog(frm, "Add Event", true);
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, 10px horizontal and vertical gaps

            // Create and add form components to the panel
            JLabel eventTitle = new JLabel("Event Title :");
            JTextField eventTitleField = new JTextField();

            JLabel dateLabel = new JLabel("Date:");
            JTextField dateField = new JTextField();
            dateField.setText(LocalDate.now().toString());

            JLabel startTimeLabel = new JLabel("Start Time:");
            JTextField startTimeField = new JTextField();
            JLabel endTimeLabel = new JLabel("End Time:");
            JTextField endTimeField = new JTextField();

            JLabel descriptionLabel = new JLabel("Description:");
            JTextArea descriptionArea = new JTextArea();
            descriptionArea.setRows(4);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

            JLabel hostLabel = new JLabel("Host:");
            JLabel participantsLabel = new JLabel("Participants:");

            // Assuming Config.names is a String array containing participant names
            JCheckBox[] participantCheckBoxes = new JCheckBox[Config.names.length];
            JPanel participantsPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Single column for participant checkboxes
            for (int i = 0; i < Config.names.length; i++) {
                if(cal.currentUser !=i) {
                    participantCheckBoxes[i] = new JCheckBox(Config.names[i]);
                    participantsPanel.add(participantCheckBoxes[i]);
                }
            }

            // Add padding to the panel
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add components to the panel

            panel.add(eventTitle);
            panel.add(eventTitleField);

            panel.add(dateLabel);
            panel.add(dateField);

            panel.add(startTimeLabel);
            panel.add(startTimeField);

            panel.add(endTimeLabel);
            panel.add(endTimeField);

            panel.add(descriptionLabel);
            panel.add(descriptionScrollPane);

            panel.add(hostLabel);
            panel.add(new JLabel(Config.names[cal.currentUser])); // Placeholder for host label, as it's a static text

            panel.add(participantsLabel);
            panel.add(participantsPanel);

            // Create and add submit button
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(submitEvent -> {
                // Extract data from the fields
                String title = eventTitleField.getText();
                String date = dateField.getText();
                String startTime = startTimeField.getText();
                String endTime = endTimeField.getText();
                String description = descriptionArea.getText();

                // Check for overlapping events
                boolean hasOverlap = false;
                for (int i = 0; i < participantCheckBoxes.length; i++) {
                    if (participantCheckBoxes[i] != null && participantCheckBoxes[i].isSelected()) {
                        for (CalendarEvent event : userEvents.get(i).events) {
                            if (event.getDate().equals(LocalDate.parse(date)) &&
                                    ((LocalTime.parse(startTime).isAfter(event.getStart()) && LocalTime.parse(startTime).isBefore(event.getEnd())) ||
                                            (LocalTime.parse(endTime).isAfter(event.getStart()) && LocalTime.parse(endTime).isBefore(event.getEnd())) ||
                                            (LocalTime.parse(startTime).isBefore(event.getStart()) && LocalTime.parse(endTime).isAfter(event.getEnd())))) {
                                hasOverlap = true;
                                break;
                            }
                        }
                        if (hasOverlap) {
                            break;
                        }
                    }
                }


                // Check if the current user has an event at the specified time
                if (!hasOverlap) {
                    for (CalendarEvent event : userEvents.get(cal.currentUser).events) {
                        if (event.getDate().equals(LocalDate.parse(date)) &&
                                ((LocalTime.parse(startTime).isAfter(event.getStart()) && LocalTime.parse(startTime).isBefore(event.getEnd())) ||
                                        (LocalTime.parse(endTime).isAfter(event.getStart()) && LocalTime.parse(endTime).isBefore(event.getEnd())) ||
                                        (LocalTime.parse(startTime).isBefore(event.getStart()) && LocalTime.parse(endTime).isAfter(event.getEnd())))) {
                            hasOverlap = true;
                            break;
                        }
                    }
                }

                // Add event if there are no conflicts
                if (!hasOverlap) {
                    ArrayList<String> UserList = new ArrayList<>();
                    ArrayList<Integer> users = new ArrayList<>();
                    users.add(cal.currentUser);
                    for (int i = 0; i < participantCheckBoxes.length; i++) {
                        if (participantCheckBoxes[i] != null && participantCheckBoxes[i].isSelected()) {
                            UserList.add(Config.names[i]);
                            users.add(i);
                        }
                    }
                    CalendarEvent newEvent = new CalendarEvent(title,
                            LocalDate.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]), Integer.parseInt(date.split("-")[2])),
                            LocalTime.of(Integer.parseInt(startTime.split(":")[0]), Integer.parseInt(startTime.split(":")[1])),
                            LocalTime.of(Integer.parseInt(endTime.split(":")[0]), Integer.parseInt(endTime.split(":")[1])),
                            description,
                            Config.names[cal.currentUser],
                            UserList,
                            Config.colors[cal.currentUser]
                    );
                    cal.addEvent(newEvent, users);


                } else {
                    JOptionPane.showMessageDialog(null, "Conflicting event! Please choose a different time.", "Error", JOptionPane.ERROR_MESSAGE);
                }


                // Close the dialog after submission
                dialog.dispose();
            });
            panel.add(submitButton);

            dialog.setSize(400, 500);
            
            int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            int frameWidth = dialog.getSize().width;
            int frameHeight = dialog.getSize().height;
            int x = (screenWidth - frameWidth) / 2;
            int y = (screenHeight - frameHeight) / 2;

            // Set the location of the frame to the center of the screen
            dialog.setLocation(x, y);


            // Add the panel to the dialog content pane
            dialog.getContentPane().add(panel);
            dialog.setVisible(true); // Make the dialog visible
        });
        return addButton;
    }
  
  static private void editEventHandler(ActionEvent editEvent, JFrame frm, Calendar cal, ArrayList<User> userEvents, CalendarEventClickEvent e){
        JDialog dialog = new JDialog(frm, "Modify Event", true);
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, 10px horizontal and vertical gaps

        
            CalendarEvent currentEvent = e.getCalendarEvent();

            System.out.println(currentEvent.getEventTitle());


            // Create and add form components to the panel
            JLabel eventTitle = new JLabel("Event Title :");
            JTextField eventTitleField = new JTextField();

            eventTitleField.setText(currentEvent.getEventTitle());

            JLabel dateLabel = new JLabel("Date:");
            JTextField dateField = new JTextField();
            LocalDate currentDate = currentEvent.getDate();
            dateField.setText(currentDate.toString());


            JLabel startTimeLabel = new JLabel("Start Time:");
            JTextField startTimeField = new JTextField();
            LocalTime currentStartTime = currentEvent.getStart();
            startTimeField.setText(currentStartTime.toString());



            JLabel endTimeLabel = new JLabel("End Time:");
            JTextField endTimeField = new JTextField();
            LocalTime currentEndTime = currentEvent.getEnd();
            endTimeField.setText(currentEndTime.toString());

            JLabel descriptionLabel = new JLabel("Description:");
            JTextArea descriptionArea = new JTextArea();
            descriptionArea.setRows(4);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            String currentDescription = currentEvent.getText();
            descriptionArea.setText(currentDescription);

            ArrayList<String> currentParticipants = currentEvent.getParticipants();


            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

            JLabel hostLabel = new JLabel("Host:");
            JLabel participantsLabel = new JLabel("Participants:");

            // Assuming Config.names is a String array containing participant names
            JCheckBox[] participantCheckBoxes = new JCheckBox[Config.names.length];
            JPanel participantsPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Single column for participant checkboxes
            for (int i = 0; i < Config.names.length; i++) {
                if(cal.currentUser !=i) {
                    participantCheckBoxes[i] = new JCheckBox(Config.names[i]);

                    if(currentParticipants.contains(Config.names[i])){
                        participantCheckBoxes[i].setSelected(true);
                    }
                    participantsPanel.add(participantCheckBoxes[i]);
                }
            }

            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add components to the panel

            panel.add(eventTitle);
            panel.add(eventTitleField);

            panel.add(dateLabel);
            panel.add(dateField);

            panel.add(startTimeLabel);
            panel.add(startTimeField);

            panel.add(endTimeLabel);
            panel.add(endTimeField);

            panel.add(descriptionLabel);
            panel.add(descriptionScrollPane);

            panel.add(hostLabel);
            panel.add(new JLabel(Config.names[cal.currentUser])); // Placeholder for host label, as it's a static text

            panel.add(participantsLabel);
            panel.add(participantsPanel);



            // Create and add submit button
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(submitEvent -> {
                // Remove events for everyone
                ArrayList<String> participants = e.getCalendarEvent().getParticipants();

                // Extract data from the fields
                String title = eventTitleField.getText();
                String date = dateField.getText();
                String startTime = startTimeField.getText();
                String endTime = endTimeField.getText();
                String description = descriptionArea.getText();

                // Check for overlapping events
                boolean hasOverlap = false;
                for (int i = 0; i < participantCheckBoxes.length; i++) {
                    if (participantCheckBoxes[i] != null && participantCheckBoxes[i].isSelected()) {
                        for (CalendarEvent event : userEvents.get(i).events) {
                            if (event.getDate().equals(LocalDate.parse(date)) &&
                                    ((LocalTime.parse(startTime).isAfter(event.getStart()) && LocalTime.parse(startTime).isBefore(event.getEnd())) ||
                                            (LocalTime.parse(endTime).isAfter(event.getStart()) && LocalTime.parse(endTime).isBefore(event.getEnd())) ||
                                            (LocalTime.parse(startTime).isBefore(event.getStart()) && LocalTime.parse(endTime).isAfter(event.getEnd())))) {
                                hasOverlap = true;
                                break;
                            }
                        }
                        if (hasOverlap) {
                            break;
                        }
                    }
                }


                // Check if the current user has an event at the specified time
                if (!hasOverlap) {
                    for (CalendarEvent event : userEvents.get(cal.currentUser).events) {
                        if (event.getDate().equals(LocalDate.parse(date)) &&
                                ((LocalTime.parse(startTime).isAfter(event.getStart()) && LocalTime.parse(startTime).isBefore(event.getEnd())) ||
                                        (LocalTime.parse(endTime).isAfter(event.getStart()) && LocalTime.parse(endTime).isBefore(event.getEnd())) ||
                                        (LocalTime.parse(startTime).isBefore(event.getStart()) && LocalTime.parse(endTime).isAfter(event.getEnd())))) {
                            hasOverlap = true;
                            break;
                        }
                    }
                }

                // Add event if there are no conflicts
                if (!hasOverlap) {
                    ArrayList<String> UserList = new ArrayList<>();
                    ArrayList<Integer> users = new ArrayList<>();
                    users.add(cal.currentUser);
                    for (int i = 0; i < participantCheckBoxes.length; i++) {
                        if (participantCheckBoxes[i] != null && participantCheckBoxes[i].isSelected()) {
                            UserList.add(Config.names[i]);
                            users.add(i);
                        }
                    }
                    cal.removeEvent(currentEvent, participants);
                    cal.addEvent(new CalendarEvent(title,
                                    LocalDate.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]), Integer.parseInt(date.split("-")[2])),
                                    LocalTime.of(Integer.parseInt(startTime.split(":")[0]), Integer.parseInt(startTime.split(":")[1])),
                                    LocalTime.of(Integer.parseInt(endTime.split(":")[0]), Integer.parseInt(endTime.split(":")[1])),
                                    description,
                                    Config.names[cal.currentUser],
                                    UserList,
                                    Config.colors[cal.currentUser]
                            ), users);                    
                } else {
                    JOptionPane.showMessageDialog(null, "Conflicting event! Please choose a different time.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Close the dialog after submission
                dialog.dispose();
            });
            panel.add(submitButton);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Add the panel to the dialog content pane
            dialog.getContentPane().add(panel);
            dialog.setSize(500, 400);
            
            
            int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            int frameWidth = dialog.getSize().width;
            int frameHeight = dialog.getSize().height;
            int x = (screenWidth - frameWidth) / 2;
            int y = (screenHeight - frameHeight) / 2;

            // Set the location of the frame to the center of the screen
            dialog.setLocation(x, y);
                
            // Set dialog size
            dialog.setVisible(true); // Make the dialog visible

    }
}
