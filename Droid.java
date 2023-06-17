//NAMA : Adityo Khori Ramadhan
//NIM : 2101020045
//PROJECT MATA KULIAH PAA, "PERMAINAN HIDE AND SEEK"
package com;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Droid extends JFrame {
  
    private Timer timer;
    private int[][] map;
    private final int numRows = 15;
    private final int numCols = 15; 
    private final int cellSize = 40;
    private JPanel mapPanel;
    private JButton generateButton;
    private JButton placeDroidGreenButton;
    private JButton placeDroidRedButton;
    private JButton DFSButton;
    private JButton SRDButton;
    private JButton SGDButton;
    private JButton XButton;
    private JButton PButton;
    private JButton RButton;
    private JButton AcakRButton;
    private JButton AcakGButton;
    private Random rand;
    private final ArrayList<Point> redDroids = new ArrayList<>(); // variabel untuk menyimpan droid merah
    private Point greenDroid; // variabel untuk menyimpan droid hijau
    private double distanceThreshold = 5;
    JSlider distanceSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
    private boolean isRunning = true;


public Droid(){
    init();
}

private void init() {
    setTitle("DROID x HUNTER");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    // membuat panel untuk peta
    mapPanel = new JPanel();
    mapPanel.setPreferredSize(new Dimension(numCols * cellSize, numRows * cellSize));
    mapPanel.setLayout(new GridLayout(numRows, numCols));
    getContentPane().add(mapPanel, BorderLayout.CENTER);
 
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(12,1));

    //TOMBOL-TOMBOL
    generateButton = new JButton("ACAK / RESET PETA");
    generateButton.addActionListener(e -> generateMap());
    buttonPanel.add(generateButton);

    placeDroidRedButton = new JButton("TAMBAH DROID MERAH");
    placeDroidRedButton.addActionListener(e -> placeDroid());
    buttonPanel.add(placeDroidRedButton);

    AcakRButton = new JButton("ACAK DROID MERAH");
    AcakRButton.addActionListener(e -> RandomRedDroid());
    buttonPanel.add(AcakRButton);

    placeDroidGreenButton = new JButton("TAMBAH DROID HIJAU");
    placeDroidGreenButton.addActionListener(e -> placeGreenDroid());
    buttonPanel.add(placeDroidGreenButton);

    AcakGButton = new JButton("ACAK DROID HIJAU");
    AcakGButton.addActionListener(e -> RandomGreenDroid());
    buttonPanel.add(AcakGButton);

    DFSButton = new JButton("MULAI SIMULASI");
    DFSButton.addActionListener(e -> searchGreenDroidDFS());
    buttonPanel.add(DFSButton);

    getContentPane().add(buttonPanel, BorderLayout.EAST);

    SRDButton = new JButton("POV DROID MERAH");
    SRDButton.addActionListener(e -> startRedTimer());
    buttonPanel.add(SRDButton);

    SGDButton = new JButton("POV DROID GREEN");
    SGDButton.addActionListener(e -> startPOVTimer());
    buttonPanel.add(SGDButton);

    PButton = new JButton("PAUSE");
    PButton.addActionListener (e -> pauseProgram());
    buttonPanel.add(PButton);

    RButton = new JButton("RESUME");
    RButton.addActionListener(e -> resumeProgram());
    buttonPanel.add(RButton);

    //Jslider untuk Atur jarak pandang droid hijau
    JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BorderLayout());

    JLabel sliderLabel = new JLabel("Atur Jarak Pandang Droid Hijau : ");
    sliderPanel.add(sliderLabel, BorderLayout.WEST);

    JSlider slider = new JSlider(0, 10, 5); // nilai awal 5, minimum 0, maksimum 20
    slider.setMajorTickSpacing(1);
    slider.setMinorTickSpacing(1);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(e -> {
        JSlider source = (JSlider) e.getSource();
        double newValue = source.getValue();
        distanceThreshold = newValue;
    });
    sliderPanel.add(slider, BorderLayout.CENTER);
    buttonPanel.add(sliderPanel);


    XButton = new JButton("KELUAR");
    XButton.addActionListener(e -> exit());
    buttonPanel.add(XButton);

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
    rand = new Random();
}

private void pauseProgram() {
    isRunning = false;
}

private void resumeProgram() {
    isRunning = true;
}

private void generateMap() {
    isRunning = true;
    // menghapus droid merah dan hijau yang telah ada
    for (Point droid : redDroids) {
        JPanel cell = (JPanel) mapPanel.getComponent(droid.x * numCols + droid.y);
        cell.removeAll();
    }
    redDroids.clear();
    if (greenDroid != null) {
        JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
        cell.removeAll();
        greenDroid = null;
    }
        map = new int[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            Arrays.fill(map[i], 1);
        }
        
        int startX = rand.nextInt(numRows);
        int startY = rand.nextInt(numCols);
        
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(startX, startY));
        while (!stack.isEmpty()) {
            Point curr = stack.peek();
            int x = curr.x;
            int y = curr.y;
            map[x][y] = 0; // tandai sel ini sebagai jalan

            // mencari tetangga yang belum dikunjungi
            ArrayList<Point> neighbors = new ArrayList<>();
            if (x > 1 && map[x - 2][y] == 1) {
                neighbors.add(new Point(x - 2, y));
            }
            if (x < numRows - 2 && map[x + 2][y] == 1) {
                neighbors.add(new Point(x + 2, y));
            }
            if (y > 1 && map[x][y - 2] == 1) {
                neighbors.add(new Point(x, y - 2));
            }
            if (y < numCols - 2 && map[x][y + 2] == 1) {
                neighbors.add(new Point(x, y + 2));
            }

            if (!neighbors.isEmpty()) {
                // memilih tetangga secara acak untuk dikunjungi
                Point next = neighbors.get(rand.nextInt(neighbors.size()));
                // membuat jalan menuju tetangga
                int nextX = next.x;
                int nextY = next.y;

                if (nextX == x - 2) {
                    map[x - 1][y] = 0;
                } else if (nextX == x + 2) {
                    map[x + 1][y] = 0;
                } else if (nextY == y - 2) {
                    map[x][y - 1] = 0;
                } else if (nextY == y + 2) {
                    map[x][y + 1] = 0;
                }
                    stack.push(next);
                } else {
                // tidak ada tetangga yang belum dikunjungi, kembali ke sel sebelumnya
                    stack.pop();
                }
            }

    // menggambar peta
    mapPanel.removeAll();
    for (int i = 0; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
            JPanel cell = new JPanel();
            cell.setOpaque(true);
            cell.setPreferredSize(new Dimension(cellSize, cellSize));
            if (map[i][j] == 1) {
               cell.setBackground(Color.blue);
               cell.setBorder(BorderFactory.createRaisedBevelBorder());
            } else {
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            mapPanel.add(cell);
        }
    }
    mapPanel.revalidate();
    mapPanel.repaint();
}

private void placeDroid() {
    // mencari sebuah sel yang merupakan jalan
    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    // menempatkan droid pada sel tersebut
    JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
    cell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.red);
    // Mengatur ukuran font droid
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    cell.add(droid, BorderLayout.CENTER);
    redDroids.add(new Point(x, y)); // menambahkan posisi droid merah ke variabel
    mapPanel.revalidate();
    mapPanel.repaint();
}
private void RandomRedDroid() {
// Menghapus droid merah sebelumnya dari peta
    for (Point droidPos : redDroids) {
        int x = droidPos.x;
        int y = droidPos.y;
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.removeAll();
        map[x][y] = 0; // Mengubah sel menjadi jalan kosong
    }
    // Mengacak posisi setiap droid merah yang ada
    for (Point droidPos : redDroids) {
        int x, y;
        do {
            x = rand.nextInt(numRows);
            y = rand.nextInt(numCols);
        } while (map[x][y] != 0);

        // Mengupdate posisi droid pada sel yang baru diacak
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.removeAll(); // Menghapus komponen yang ada pada sel
        cell.setLayout(new BorderLayout());
        JLabel droid = new JLabel("●", SwingConstants.CENTER);
        droid.setForeground(Color.red);
        // Mengatur ukuran font droid
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        droid.setFont(droidFont);
        cell.add(droid, BorderLayout.CENTER);

        // Mengupdate posisi droid merah dalam ArrayList
        droidPos.setLocation(x, y);
    }

    mapPanel.revalidate();
    mapPanel.repaint();
}

private void placeGreenDroid() {
if (greenDroid != null) {
        return; // sudah ada droid hijau pada peta
    }
    // mencari sebuah sel yang merupakan jalan
    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    // menempatkan droid hijau pada sel tersebut
    JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
    cell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.GREEN);
    // Mengatur ukuran font droid
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    cell.add(droid, BorderLayout.CENTER);
    greenDroid = new Point(x, y); // menyimpan posisi droid hijau ke variabel
    mapPanel.revalidate();
    mapPanel.repaint();
}

private void RandomGreenDroid() {
    // Menghapus droid hijau sebelumnya dari peta
    if (greenDroid != null) {
        int x = greenDroid.x;
        int y = greenDroid.y;
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.removeAll();
        map[x][y] = 0; // Mengubah sel menjadi jalan kosong
        greenDroid = null; // Menghapus referensi droid hijau sebelumnya
    }

    // Mengacak posisi baru untuk droid hijau
    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    // Menempatkan droid hijau pada posisi yang baru diacak
    JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
    cell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.GREEN);
    // Mengatur ukuran font droid
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    cell.add(droid, BorderLayout.CENTER);
    greenDroid = new Point(x, y); // Menyimpan posisi droid hijau ke variabel
    map[x][y] = 2; // Mengubah sel menjadi droid hijau

    mapPanel.revalidate();
    mapPanel.repaint();
}

private void startRedTimer() {
    timer = new Timer(100, e -> povRED());
    timer.start();
}

private void povRED() {
    // menampilkan hanya droid merah pada peta
    for (Point redDroid : redDroids) {
        JPanel cell = (JPanel) mapPanel.getComponent(redDroid.x * numCols + redDroid.y);
        cell.setLayout(new BorderLayout());
        JLabel droid = new JLabel("●", SwingConstants.CENTER);
        droid.setForeground(Color.red);
        // Mengatur ukuran font droid
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        droid.setFont(droidFont);
        
    }
    // menampilkan droid hijau pada posisinya
    if (greenDroid != null) {
        JPanel Gcell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
        Gcell.removeAll();
        JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);        
        cell.setLayout(new BorderLayout());
        JLabel droid = new JLabel("●", SwingConstants.CENTER);
        droid.setForeground(Color.WHITE);
        // Mengatur ukuran font droid
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        droid.setFont(droidFont);
        
    }
    mapPanel.revalidate();
    mapPanel.repaint();
}

// jarak pandang droid hijau (dalam sel)
private void startPOVTimer() {
    timer = new Timer(100, e -> povGREEN());
    timer.start();
}

private void povGREEN() {
    // Menghapus semua warna sebelumnya
    for (Component component : mapPanel.getComponents()) {
        JPanel cell = (JPanel) component;
        cell.setBackground(Color.white); // Ganti dengan warna latar belakang yang diinginkan
        cell.setOpaque(true); // Mengatur opaqueness menjadi true untuk mengembalikan opaqueness yang mungkin telah diubah sebelumnya
    }
    
    // Membuat lingkaran sudut pandang droid hijau yang baru
    double radius = distanceThreshold; // Jarak sudut pandang droid hijau
    int centerX = greenDroid.x;
    int centerY = greenDroid.y;

    // Mengubah warna sel-sel yang berada di luar sudut pandang droid hijau menjadi hitam
    for (int i = 0; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
            JPanel cell = (JPanel) mapPanel.getComponent(i * numCols + j);
            double distance = Math.sqrt(Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2));
            if (distance > radius) {
                cell.setBackground(Color.black);   
            }
            if (distance <= radius) {
                if (map[i][j] == 1) {
                    cell.setBackground(Color.blue);
                }
            }
        }
    }
    
    // Mengubah warna sel-sel yang telah dikunjungi oleh droid merah menjadi transparan
    for (Point p : redDroids) {
        int x = p.x;
        int y = p.y;
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        if (distance <= radius) {
            JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
            cell.setOpaque(false);
        }
    }
    
    mapPanel.revalidate();
    mapPanel.repaint();
}

private void exit() {
  System.exit(0);
}

private double calculateEuclideanDistance(Point p1, Point p2) {
    int dx = p2.x - p1.x;
    int dy = p2.y - p1.y;
    return Math.sqrt(dx * dx + dy * dy);
}

private void searchGreenDroidDFS() {
    for (Point redDroid : redDroids) {
        Thread searchThread = new Thread(() -> {
        boolean[][] visited = new boolean[numRows][numCols];
        Stack<Point> stack = new Stack<>();
        // Menggunakan droid merah pertama sebagai titik awal
        stack.push(redDroid);
        visited[redDroid.x][redDroid.y] = true;

        Timer timer = new Timer(180, e -> {
        if (isRunning) {
            if (!stack.isEmpty()) {
                Point current = stack.peek();
                JPanel currentCell = (JPanel) mapPanel.getComponent(current.x * numCols + current.y);

                // Pencarian tetangga yang merupakan jalur jalan
                ArrayList<Point> neighbors = new ArrayList<>();
                int x = current.x;
                int y = current.y;

                if (x > 0 && map[x - 1][y] == 0 && !visited[x - 1][y]) {
                    neighbors.add(new Point(x - 1, y));
                }
                if (x < numRows - 1 && map[x + 1][y] == 0 && !visited[x + 1][y]) {
                    neighbors.add(new Point(x + 1, y));
                }
                if (y > 0 && map[x][y - 1] == 0 && !visited[x][y - 1]) {
                    neighbors.add(new Point(x, y - 1));
                } 
                if (y < numCols - 1 && map[x][y + 1] == 0 && !visited[x][y + 1]) {
                    neighbors.add(new Point(x, y + 1));
                }

                if (!neighbors.isEmpty()) {
                    // Memilih tetangga secara acak untuk dijelajahi selanjutnya
                    Point next = getClosestNeighbor(neighbors, greenDroid);
                    visited[next.x][next.y] = true;
                    stack.push(next);
                    JPanel nextCell = (JPanel) mapPanel.getComponent(next.x * numCols + next.y);

                    // Menghapus droid merah pada sel sebelumnya
                    currentCell.removeAll();
                    currentCell.revalidate();
                    currentCell.repaint();

                    // Menambahkan droid merah pada sel berikutnya
                    nextCell.setLayout(new BorderLayout());
                    JLabel droid = new JLabel("●", SwingConstants.CENTER);
                    droid.setForeground(Color.red);
                    // Mengatur ukuran font droid
                    Font droidFont = new Font("Arial", Font.BOLD, 40);
                    droid.setFont(droidFont);
                    nextCell.add(droid, BorderLayout.CENTER);
                    nextCell.revalidate();
                    nextCell.repaint();
                // Jika droid merah bertemu dengan droid hijau, berhenti
                if (next.equals(greenDroid)&& isRunning) {
                    
                    ((Timer) e.getSource()).stop();
                    isRunning = false;
                    JOptionPane.showMessageDialog(null, "Droid merah berhasil menemukan droid hijau!");
                    return;
                }
                    // Perhitungan jarak Euclidean
                    double distance = calculateEuclideanDistance(next, greenDroid);

                    // Menggerakkan droid hijau berdasarkan jarak Euclidean
                    moveGreenDroid(distance);
                } else {
                    // Tidak ada tetangga yang belum dikunjungi, mundur menggunakan backtracking
                    stack.pop();

                    // Menghapus droid merah pada sel saat melakukan backtracking
                    currentCell.removeAll();
                    currentCell.revalidate();
                    currentCell.repaint();

                    // Menambahkan droid merah kembali pada sel saat melakukan backtracking
                    JPanel prevCell = (JPanel) mapPanel.getComponent(stack.peek().x * numCols + stack.peek().y);
                    prevCell.setLayout(new BorderLayout());
                    JLabel droid = new JLabel("●", SwingConstants.CENTER);
                    droid.setForeground(Color.red);
                    // Mengatur ukuran font droid
                    Font droidFont = new Font("Arial", Font.BOLD, 40);
                    droid.setFont(droidFont);
                    prevCell.add(droid, BorderLayout.CENTER);
                    prevCell.revalidate();
                    prevCell.repaint();

                    // Perhitungan jarak Euclidean setelah backtracking
                    double distance = calculateEuclideanDistance(stack.peek(), greenDroid);

                    // Menggerakkan droid hijau berdasarkan jarak Euclidean
                    moveGreenDroid(distance);
                    }
                } 
                else {
                // Jika stack kosong, berarti pencarian selesai tanpa menemukan droid hijau
                ((Timer) e.getSource()).stop();
                JOptionPane.showMessageDialog(null, "Droid merah tidak dapat menemukan droid hijau.");
                }
            }
         });
        // Memulai Timer untuk pergerakan droid merah
        timer.start();
      });
    searchThread.start();
  }
}
private Point getClosestNeighbor(ArrayList<Point> neighbors, Point target) {
    Point closest = neighbors.get(0);
    double closestDistance = calculateEuclideanDistance(closest, target);

    for (Point neighbor : neighbors) {
        double distance = calculateEuclideanDistance(neighbor, target);
        if (distance < closestDistance) {
            closest = neighbor;
            closestDistance = distance;
        }
    }

    return closest;
}
   
private void moveGreenDroid(double distance) {
    if (distance < distanceThreshold) {
        ArrayList<Point> moves = new ArrayList<>();
        int x = greenDroid.x;
        int y = greenDroid.y;
        int redX = redDroids.get(0).x;  // Koordinat X droid merah
        int redY = redDroids.get(0).y;  // Koordinat Y droid merah

        if (x > 0 && map[x - 1][y] == 0 && (x - 1 != redX || y != redY) && !isVisitedByRed(x - 1, y)) {
            moves.add(new Point(x - 1, y));
        }
        if (x < numRows - 1 && map[x + 1][y] == 0 && (x + 1 != redX || y != redY) && !isVisitedByRed(x + 1, y)) {
            moves.add(new Point(x + 1, y));
        }
        if (y > 0 && map[x][y - 1] == 0 && (x != redX || y - 1 != redY) && !isVisitedByRed(x, y - 1)) {
            moves.add(new Point(x, y - 1));
        }
        if (y < numCols - 1 && map[x][y + 1] == 0 && (x != redX || y + 1 != redY) && !isVisitedByRed(x, y + 1)) {
            moves.add(new Point(x, y + 1));
        }


        if (!moves.isEmpty()) {
        // Memilih gerakan yang menjauhi droid merah
            Point farthestMove = moves.get(0);
            double farthestDistance = calculateEuclideanDistance(farthestMove, redDroids.get(0));

            for (Point move : moves) {
                double moveDistance = calculateEuclideanDistance(move, redDroids.get(0));
                if (moveDistance > farthestDistance) {
                    farthestMove = move;
                    farthestDistance = moveDistance;
                }
            }

            // Melakukan pergerakan hanya jika langkah menjauhi droid merah
            if (farthestDistance > calculateEuclideanDistance(redDroids.get(0), greenDroid)) {
                JPanel currentCell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
                JPanel nextCell = (JPanel) mapPanel.getComponent(farthestMove.x * numCols + farthestMove.y);
                // Menghapus droid hijau pada sel sebelumnya
                currentCell.removeAll();
                currentCell.revalidate();
                currentCell.repaint();

                // Menambahkan droid hijau pada sel berikutnya
                nextCell.setLayout(new BorderLayout());
                JLabel droid = new JLabel("●", SwingConstants.CENTER);
                droid.setForeground(Color.green);
                // Mengatur ukuran font droid
                Font droidFont = new Font("Arial", Font.BOLD, 40);
                droid.setFont(droidFont);
                nextCell.add(droid, BorderLayout.CENTER);
                nextCell.revalidate();
                nextCell.repaint();
                greenDroid = farthestMove;

            }
        }
    }  
}
private boolean isVisitedByRed(int x, int y) {
    for (Point redDroid : redDroids) {
        if (redDroid.x == x && redDroid.y == y) {
            return true;
        }
    }
    return false;
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(Droid::new);
   }
}

