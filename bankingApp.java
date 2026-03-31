import java.awt.*;
import javax.swing.*;

public class bankingApp {
	 // Global 
	static JFrame frame = new JFrame("Banking App");
	static JLabel bal, withdrawBal;
	
	// CardLayout setup
	static CardLayout cardLayout = new CardLayout();	// Controls/switches all screens
	static JPanel container = new JPanel(cardLayout);	// Holds all the screens

	
	public static void updateBalanceLabel(double balance) {
		bal.setText(String.format("Your Balance is: $%.2f", balance));
}
	public static void updateWithdrawBalance(double balance) {
		withdrawBal.setText(String.format("Your Balance is: $%.2f", balance));
	}
	public static void frame() {
		
		// Create screens
		JPanel mainMenu = createMainMenu();
		JPanel newUserMenu = createNewUserMenu();
		JPanel returningUserMenu = returningUserMenu();

		
		// Add screens to container & execute method in menu
		container.add(mainMenu, "menu");			// first panel added main menu default panel to open first
		container.add(newUserMenu, "New User");		// new user screen
		container.add(returningUserMenu, "Returning User");	// returning user
		
		frame.add(container);
		frame.setSize(500,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
	}
	
	// Create main menu interface
	private static JPanel createMainMenu() {
		JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(5,2,10,10));

		// Welcome Screen
		JLabel welcome = new JLabel("Welcome To Millionaires Banking App!", SwingConstants.CENTER);
		
		// New User Button
		JButton newUserBtn = new JButton("NEW USER");
		// Returning User Button
		JButton returningUserBtn = new JButton("RETURNING USER");
	
		returningUserBtn.addActionListener(e->{
			cardLayout.show(container, "Returning User");
		});
		newUserBtn.addActionListener(e-> {
			cardLayout.show(container, "New User");	// switches screen to new user screen
		});
		
		panel.add(welcome);
		panel.add(newUserBtn);
		panel.add(returningUserBtn);

		return panel;
	}
	
	// New user screen
public static JPanel createNewUserMenu() {
		JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	    panel.setPreferredSize(new Dimension(350,250));
	    
		JLabel welcome = new JLabel("Welcome! Please Create your Account!", SwingConstants.CENTER);
        JLabel usernameLabel = new JLabel("Enter your username");
        JTextField usernameBox = new JTextField();
        JLabel generateNumberLabel = new JLabel("Generate your Random Balance & Account Number");
        JButton generate = new JButton("Generate");
        // Set size & customize before giving invinisiblity        
        generate.setVisible(false);
		JButton backBtn = new JButton("Back");
		JButton confirmUN = new JButton("Confirm Username");
        // Text Alignment
        usernameBox.setHorizontalAlignment(JTextField.CENTER);
            	
    	// Confirm User name button
        // User name validation, check if user name already exists in the database
       confirmUN.addActionListener(p->{
    	  // Check if user name is taken
    	   String username = usernameBox.getText().trim();
    	   
    	   boolean check = DB.exists(username);
    	   // Validate if username exists
           if(check) {
       		JOptionPane.showMessageDialog(null, "Username is taken. Try again");
       		generate.setVisible(false);
           } else if (!check)
           {	// User name is available
           	// Generate button to generate a new account number in DB
        	JOptionPane.showMessageDialog(null, "Username is available!");
        	generate.setVisible(true);
           }
       });
       generate.addActionListener(e->{
    	   // Get user name again
    	   String username = usernameBox.getText().trim();
    	   // Check validation again
    	   if(username.isEmpty()) {
           	JOptionPane.showMessageDialog(null, "Enter your username");
           	return;
    	   }
    	   if(DB.exists(username) == true) {
          		JOptionPane.showMessageDialog(null, "Username is taken. Try again");
          		generate.setVisible(false);
          		return;
    	   }
    	   	// Get a random balance   
          	double balance = DB.generateBalance();
          	// Transfer balance to the database and call for a new account number for this user
          	int newAccountNumber = DB.newUser(username, balance);
          	// Now put generated values and user inputed user name into ONLY IF != -1
          	if(newAccountNumber != -1) {
          	userMenu(username, newAccountNumber, balance);
          	} else {
           		JOptionPane.showMessageDialog(null, "Error creating account");
          	}
          	});
        		
        backBtn.addActionListener(e->{
        	cardLayout.show(container, "menu");		// Go back to menu button
        });
        // Alignment for returning user menu prompt
        panel.add(Box.createVerticalStrut(20));
    	welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcome);
        
    	panel.add(Box.createVerticalStrut(10));
    	usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameLabel);

    	panel.add(Box.createVerticalStrut(10));
    	usernameBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usernameBox);

    	panel.add(Box.createVerticalStrut(10));
    	generateNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(generateNumberLabel);

    	panel.add(Box.createVerticalStrut(10));
    	confirmUN.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmUN);
        
        panel.add(Box.createVerticalStrut(10));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        panel.add(backBtn);
        
        panel.add(Box.createVerticalStrut(10));
        generate.setAlignmentX(Component.CENTER_ALIGNMENT); 
        panel.add(generate);
        
        panel.revalidate();
        panel.repaint();
        
        // Wrapper panel for centering all components of the frame
   	    JPanel wrapper = new JPanel(new GridBagLayout());
   	    wrapper.add(panel);
   		return wrapper;
   		}
// Load Existing User Screen
public static JPanel returningUserMenu() {		
		
		// Layout organization + format
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
	    panel.setPreferredSize(new Dimension(350,250));
	    
		JLabel welcome = new JLabel("Welcome!", SwingConstants.CENTER);
        JTextField usernameBox = new JTextField();
        JTextField accountNumberBox = new JTextField();
        // Text alignment 
        usernameBox.setHorizontalAlignment(JTextField.CENTER);
        accountNumberBox.setHorizontalAlignment(JTextField.CENTER);

        JButton login = new JButton("Login");
        JButton backBtn = new JButton("Back");
        
        login.addActionListener(e->{
        	String username = usernameBox.getText().trim();
        	String accountText = accountNumberBox.getText().trim();
         try {
        
        	int accountNumber = Integer.parseInt(accountText);
        	// Checks if User exists in the database
        	double balance = DB.existingUser(username, accountNumber);	// Getting balance from database

        	if (balance >= 0) {	// Valid        		
        		userMenu(username, accountNumber, balance);
        	} else {
        		JOptionPane.showMessageDialog(null, "Wrong User name or Account Number");
        	}
        }catch (NumberFormatException ex) {
        	JOptionPane.showMessageDialog(null, "Error");
        }
        });
        
        backBtn.addActionListener(e->{
        	cardLayout.show(container, "menu");		// Go back to menu button
        });
     // Alignment for returning user menu prompt
    	panel.add(Box.createVerticalStrut(20));
    	welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcome);

    	usernameBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(new JLabel("Username: "));
        panel.add(usernameBox);

    	accountNumberBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(new JLabel("Account Number: "));
        panel.add(accountNumberBox);
        
    	panel.add(Box.createVerticalStrut(10));
    	login.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(login);

    	panel.add(Box.createVerticalStrut(10));
    	backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(backBtn);
        
        // Wrapper panel for centering all components of the frame
	    JPanel wrapper = new JPanel(new GridBagLayout());
	    wrapper.add(panel);
		return wrapper;
	}
// Deposit Menu
public static JPanel depositMenu(String username, int accountNumber) {
		JPanel panel = new JPanel(new BorderLayout(10,10));
		JPanel top = new JPanel(new GridLayout(3,1,10,10));

		
		JLabel title = new JLabel("Deposit Funds", SwingConstants.CENTER);
		
		JTextField amount = new JTextField();
		amount.setEditable(false);
		amount.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		
		// Key pads for user input
		JPanel keys = new JPanel(new GridLayout(4,3,10,10));
		String[] num = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "C"};
		
		// For loop to print all nums in array, num.length = number of elements in array
		for(int i = 0; i < num.length; i++) 
		{
			String value = num[i];
			JButton butn = new JButton(num[i]);
			
			butn.addActionListener(e->{
				// Holds number to later be concatinated/parsed to double/int
				String currentNum = amount.getText();
				
				if(value.equals("C"))
				{
					amount.setText("");	// for when C = clear is pressed clear the box of all integers
				} else {
					amount.setText((currentNum) + value);
				}
			});
			keys.add(butn);
		}
		// Buttons for confirmation
		JButton confirmBtn = new JButton("Confirm");
		JButton backBtn = new JButton("Back");
		
		backBtn.addActionListener(g->{
			cardLayout.show(container, "user menu");
		});
		
		confirmBtn.addActionListener(p->{
		try {
			double amountEntered = Double.parseDouble(amount.getText());
			
			// Input Validation
			if (amountEntered <= 0) {
                JOptionPane.showMessageDialog(null, "Enter a valid deposit amount");
                return;
            }
			// Fetch current balance and calculate and update
			double balance = DB.existingUser(username, accountNumber);
			double newBalance = balance + amountEntered;
			DB.updateBalance(username, accountNumber, newBalance);
			
			// Update JLabel displaying balance on the screen
			updateBalanceLabel(newBalance);
			cardLayout.show(container, "user menu");
			JOptionPane.showMessageDialog(null, String.format("Deposit Successful. New Balance: $" + newBalance));
		
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Invalid Amount.");
		}
		});
		
		// Top for the top of the frame
		top.add(title);
		top.add(amount);	
		
		// Bottom means bottom of the frame, (1,2,10,10) lower means low on the window
		JPanel bottom = new JPanel(new GridLayout(1,2,10,10));
		bottom.add(confirmBtn);
		bottom.add(backBtn);
		
		panel.add(top, BorderLayout.NORTH);
		panel.add(keys, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		return panel;
	}
// Withdraw Menu
public static JPanel withdrawMenu(String username, int accountNumber) {

	JPanel panel = new JPanel(new BorderLayout(10,10));
	JPanel top = new JPanel(new GridLayout(3,1,10,10));

		JLabel title = new JLabel("Withdraw Funds", SwingConstants.CENTER);
		withdrawBal = new JLabel(
			    String.format("Current Balance: $%.2f", DB.existingUser(username, accountNumber)),
			    SwingConstants.CENTER
			);
		JTextField amount = new JTextField();
		amount.setEditable(false);
		amount.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		
		// Key pads for user input
		JPanel keys = new JPanel(new GridLayout(4,3,10,10));
		String[] num = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "C"};
		
		// For loop to print all nums in array, num.length = number of elements in array
		for(int i = 0; i < num.length; i++) 
		{
			String value = num[i];
			JButton butn = new JButton(num[i]);
			
			butn.addActionListener(e->{
				// Holds number to later be concatinated/parsed to double/int
				String currentNum = amount.getText();
				
				if(value.equals("C"))
				{
					amount.setText("");	// for when C = clear is pressed clear the box of all integers
				} else {
					amount.setText((currentNum) + value);
				}
			});
			keys.add(butn);
		}
		// Buttons for confirmation
		JButton confirmBtn = new JButton("Confirm");
		JButton backBtn = new JButton("Back");
		
		// Back Button
		backBtn.addActionListener(g->{
			cardLayout.show(container, "user menu");
		});
	
		
		// Confirm withdraw button
		confirmBtn.addActionListener(p->{
		try {
			// Input Validation
			double amountEntered = Double.parseDouble(amount.getText());
			// Input Validation
			if (amountEntered <= 0) {
	           JOptionPane.showMessageDialog(null, "Enter a valid withdrawal amount");
	           return;
	            }
			
			double balance = DB.existingUser(username, accountNumber);
			// Validation
			if(amountEntered > balance) {
				JOptionPane.showMessageDialog(null, "Invalid Amount. Amount Entered is Greater than your Current Balance");
				return;
			}
			
			double newBalance = balance - amountEntered;
			// Update the database
			DB.updateBalance(username, accountNumber, newBalance);
			// Update label
			updateBalanceLabel(newBalance);
			updateWithdrawBalance(newBalance);

			cardLayout.show(container, "user menu");
			JOptionPane.showMessageDialog(null, "Withdraw Successful. New Balance: $" + newBalance);
		
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Invalid Amount.");
		}
		});
		// Top for the top of the frame
		top.add(title);
		top.add(withdrawBal);
		top.add(amount);
		
		// Bottom means bottom of the frame, (1,2,10,10) lower means low on the window
		JPanel bottom = new JPanel(new GridLayout(1,2,10,10));
		bottom.add(confirmBtn);
		bottom.add(backBtn);
		
		panel.add(top, BorderLayout.NORTH);
		panel.add(keys, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);
		return panel;
	}
public static void userMenu(String username, int accountNumber, double balance) {
	// Welcoming new User and displaying their information
	// Layout organization + format
	JPanel userMenu = new JPanel();
	userMenu.setLayout(new BoxLayout(userMenu, BoxLayout.Y_AXIS));
    Dimension size = new Dimension(200,50);
    
	JLabel welcomeUser = new JLabel("<html><center>Successful Login!<br>Welcome! " + username 
			+ "!</center></html>", SwingConstants.CENTER);
	JLabel aNumber = new JLabel("Account Number: " + accountNumber, SwingConstants.CENTER);
	
	bal = new JLabel("", SwingConstants.CENTER);
	updateBalanceLabel(balance);
	
	JButton depo = new JButton("Deposit");
	JButton widraw = new JButton("Withdraw");
	JButton logout = new JButton("Logout");
	
	// Make deposit panel
	JPanel depositScreen = depositMenu(username, accountNumber);
	container.remove(depositScreen);
	container.add(depositScreen, "deposit menu");
	// Make withdraw panel
	JPanel withdrawScreen = withdrawMenu(username, accountNumber);
	container.remove(withdrawScreen);
	container.add(withdrawScreen, "withdraw menu");
	
	// Add functionality to buttons through methods
	// Deposit
	depo.addActionListener(d->{
		cardLayout.show(container, "deposit menu");
	});
	// Withdraw
	widraw.addActionListener(g->{
		cardLayout.show(container, "withdraw menu");
	});
	// Log out
	logout.addActionListener(m->{
		cardLayout.show(container, "menu");
	});
	
	// Sizes & Font
		// Buttons
	Font buttonFont = new Font("Arial", Font.BOLD, 20);
	depo.setMaximumSize(size);
	widraw.setMaximumSize(size);
	logout.setMaximumSize(size);
	depo.setFont(buttonFont);
	widraw.setFont(buttonFont);
	logout.setFont(buttonFont);
		// Labels & || color
	welcomeUser.setFont(new Font("Arial", Font.BOLD, 24));
	welcomeUser.setForeground(new Color(0, 102, 204));
	aNumber.setFont(new Font("Arial", Font.BOLD, 24));
	bal.setFont(new Font("Arial", Font.BOLD, 24));
	
	
	// Alignment for successful login, main user interface menu
	userMenu.add(Box.createVerticalStrut(30));	
	userMenu.add(welcomeUser);
	welcomeUser.setAlignmentX(Component.CENTER_ALIGNMENT);
	
	userMenu.add(Box.createVerticalStrut(10));
	aNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
	userMenu.add(aNumber);
	
	userMenu.add(Box.createVerticalStrut(10));
	bal.setAlignmentX(Component.CENTER_ALIGNMENT);
	userMenu.add(bal);

	userMenu.add(Box.createVerticalStrut(20));
	depo.setAlignmentX(Component.CENTER_ALIGNMENT);
	userMenu.add(depo);

	userMenu.add(Box.createVerticalStrut(10));	
	widraw.setAlignmentX(Component.CENTER_ALIGNMENT);
	userMenu.add(widraw);

	userMenu.add(Box.createVerticalStrut(10));
	logout.setAlignmentX(Component.CENTER_ALIGNMENT);
	userMenu.add(logout);
	
	
	container.add(userMenu, "user menu");
	cardLayout.show(container, "user menu");
	container.revalidate();
	container.repaint();
	
}
}
