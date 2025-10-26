//Tic_Tac_Toe Game
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tic_Tac_Toe extends JFrame implements ActionListener {
    private final JButton[][] buttons = new JButton[3][3];
    private final char[][] board = new char[3][3];
    private boolean isPlayerTurn = true;
    private final char PLAYER = 'X';
    private final char COMPUTER = 'O';

    private static final Color BG_COLOR = new Color(20, 0, 40);
    private static final Color X_COLOR = new Color(50, 200, 255);
    private static final Color O_COLOR = new Color(255, 50, 100);
    private static final Font SYMBOL_FONT = new Font("SansSerif", Font.BOLD, 100);

    public Tic_Tac_Toe() 
    {
        setTitle("Man VS Computer");
        setBounds(230, 5, 950,725);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new NeonGridPanel();
        mainPanel.setLayout(new GridLayout(3, 3, 5, 5));
        
        initializeBoard(mainPanel);
        add(mainPanel);
        getContentPane().setBackground(BG_COLOR); 
        
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class NeonGridPanel extends JPanel {
        public NeonGridPanel() {
            this.setBackground(BG_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(new Color(150, 0, 255, 100));
            g2d.setStroke(new BasicStroke(2));

            int w = getWidth();
            int h = getHeight();
            int cellW = w/3;
            int cellH = h/3;
            
            g2d.drawLine(cellW, 0, cellW, h);
            g2d.drawLine(cellW * 2, 0, cellW * 2, h);
            
            g2d.drawLine(0, cellH, w, cellH);
            g2d.drawLine(0, cellH * 2, w, cellH * 2);
        }
    }

    private void initializeBoard(JPanel mainPanel) 
    {
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                JButton btn = new JButton("");
                btn.setFont(SYMBOL_FONT);
                btn.setFocusable(false);
                btn.addActionListener(this);
                
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1));
                
                buttons[i][j] = btn;
                mainPanel.add(btn);
                board[i][j] = ' '; 
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (!isPlayerTurn) 
        return;
        JButton clickedButton = (JButton) e.getSource();
        
        int r = -1, c = -1;
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                if (buttons[i][j] == clickedButton) 
                {
                    r = i;
                    c = j;
                    break;
                }
            }
        }

        if (board[r][c] == ' ') 
        {
            makeMove(r, c, PLAYER);
            
            if (checkGameOver() == 2) 
            {
                isPlayerTurn = false;
                disableButtons(); 

                Timer timer = new Timer(1000, new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        computerMove();
                        enableButtons(); 
                        ((Timer)e.getSource()).stop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void makeMove(int r, int c, char player) 
    {
        board[r][c] = player;
        buttons[r][c].setText(String.valueOf(player));
        
        buttons[r][c].setForeground(player == PLAYER ? X_COLOR : O_COLOR); 
        
        if (player == PLAYER) {
             buttons[r][c].putClientProperty("JComponent.outline", X_COLOR);
        } else {
             buttons[r][c].putClientProperty("JComponent.outline", O_COLOR);
        }
    }

    private void computerMove() 
    {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                if (board[i][j] == ' ') 
                {
                    board[i][j] = COMPUTER; 
                    
                    int score = alphaBeta(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    
                    board[i][j] = ' '; 

                    if (score > bestScore) 
                    {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        if (bestMove[0] != -1) 
        {
            makeMove(bestMove[0], bestMove[1], COMPUTER);
        }
        checkGameOver();
        isPlayerTurn = true;
    }

    private int alphaBeta(char[][] currentBoard, int depth, int alpha, int beta, boolean isMaximizingPlayer) 
    {
        int result = checkGameOverSilent();
        if (result != 2) {
            if (result == 1) 
            return 10 - depth;
            if (result == -1) 
            return -10 + depth; 
            return 0;
        }

        if (isMaximizingPlayer) 
        {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) 
            {
                for (int j = 0; j < 3; j++) 
                {
                    if (currentBoard[i][j] == ' ') 
                    {
                        currentBoard[i][j] = COMPUTER;
                        bestScore = Math.max(bestScore, alphaBeta(currentBoard, depth + 1, alpha, beta, false));
                        currentBoard[i][j] = ' '; 
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) return bestScore;
                    }
                }
            }
            return bestScore;
        } 
        else 
        {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) 
            {
                for (int j = 0; j < 3; j++) 
                {
                    if (currentBoard[i][j] == ' ') 
                    {
                        currentBoard[i][j] = PLAYER;
                        bestScore = Math.min(bestScore, alphaBeta(currentBoard, depth + 1, alpha, beta, true));
                        currentBoard[i][j] = ' '; 
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) return bestScore;
                    }
                }
            }
            return bestScore;
        }
    }

    private int checkGameOverSilent() 
    {
        if (checkWin(COMPUTER)) 
        return 1; 
        if (checkWin(PLAYER)) 
        return -1; 
        if (isBoardFull()) 
        return 0; 
        return 2;
    }
    
    private int checkGameOver() 
    {
        String message = null;
        int result = checkGameOverSilent();

        if (result != 2) 
        {
            if (result == 1) 
            {
                message = "Computer Win!";
            } 
            else if (result == -1) 
            {
                message = "You Win!";
            } 
            else 
            {
                message = "Draw";
            }
            disableButtons();
            JOptionPane.showMessageDialog(this, message);
        }
        return result;
    }

    private boolean checkWin(char player) 
    {
        for (int i = 0; i < 3; i++) 
        {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) 
            return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) 
            return true;
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) 
        return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) 
        return true;
        return false;
    }

    private boolean isBoardFull() 
    {
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                if (board[i][j] == ' ') 
                return false;
            }
        }
        return true;
    }
    
    private void disableButtons() 
    {
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                buttons[i][j].setEnabled(false);
            }
        }
    }
    
    private void enableButtons() 
    {
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                if (board[i][j] == ' ') { 
                    buttons[i][j].setEnabled(true);
                }
            }
        }
    }
    public static void main(String[] args) 
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(Tic_Tac_Toe::new);
    }
}