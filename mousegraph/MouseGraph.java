package mousegraph;

/**
 *
 * @author BHAVYA
 */

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;
import java.util.Scanner;
import java.awt.*;
import java.io.*;
import java.awt.geom.GeneralPath;


class Error
{
    static void display(String s)
    {
        JFrame jam = new JFrame();
        JOptionPane.showMessageDialog(jam,s);
        jam.dispose();
    }
}

class Point
{
    int x,y;
    public Point(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
    int getX()
    {
        return this.x;
    }
    int getY()
    {
        return this.y;
    }
}

class Check
{
    public static boolean inLine(Point A, Point B, Point C) 
    {
        double distAB = Math.sqrt((A.x+7-B.x)*(A.x+7-B.x) + (A.y+7-B.y)*(A.y+7-B.y));
        double distAC = Math.sqrt((A.x-C.x)*(A.x-C.x) + (A.y-C.y)*(A.y-C.y));
        double distBC = Math.sqrt((C.x+7-B.x)*(C.x+7-B.x) + (C.y+7-B.y)*(C.y+7-B.y));
        return Math.abs(distAB+distBC-distAC)<0.3f;
    }
}

class Node
{
    private String name;
    private double wt;
    public Node(String name,double wt)
    {
        this.name=name;
        this.wt=wt;
    }
    public void setName(String s)
    {
        this.name=s;
    }
    public String getName()
    {
        return this.name;
    }
    public void setWt(double s)
    {
        this.wt=s;
    }
    public double getWt()
    {
        return this.wt;
    }
    
}

class Shapes
{
    public static GeneralPath plus(int x,int y)
    {
        int x1Points[] = {x-2,x-2,x+2,x+2,x+17,x+17,x+2,x+2,x-2,x-2,x-17,x-17};
        int y1Points[] = {y+2,y+17,y+17,y+2,y+2,y-2,y-2,y-17,y-17,y-2,y-2,y+2};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,x1Points.length);
        polygon.moveTo(x1Points[0],y1Points[0]);
        for(int i = 1;i<12;i++)
        {
            polygon.lineTo(x1Points[i],y1Points[i]);
        }
        polygon.closePath();
        return polygon;
    }
    
    public static GeneralPath cross(int x, int y)
    {
        int x1Points[] = {x-2,x-16,x-16,x,x+16,x+16,x+2,x+16,x+16,x,x-16,x-16};
        int y1Points[] = {y,y+16,y+16,y+2,y+16,y+16,y,y-16,y-16,y-2,y-16,y-16};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,x1Points.length);
        polygon.moveTo(x1Points[0],y1Points[0]);
        for(int i = 1;i<x1Points.length;i++)
        {
            polygon.lineTo(x1Points[i],y1Points[i]);
        }
        polygon.closePath();
        return polygon;
    }
    
    public static GeneralPath triangle(int x,int y)
    {
        int x1Points[] = {x,x+7,x-7};
        int y1Points[] = {y+7,y-7,y-7};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,x1Points.length);
        polygon.moveTo(x1Points[0],y1Points[0]);
        for(int i = 1;i<x1Points.length;i++)
        {
            polygon.lineTo(x1Points[i],y1Points[i]);
        }
        polygon.closePath();
        return polygon;
    }
}



class Dijkstra
{
    static LinkedList<String> solve(Map<String,LinkedList<Node>> m, String from, String to)
    {
        
        LinkedList<String> vertices= new LinkedList<String>(); 
        
        for(Map.Entry<String,LinkedList<Node>> it:m.entrySet())
        {
            vertices.add(it.getKey());
        }
        
        Map<String,Integer> vis= new TreeMap<String,Integer>();
        Map<String,Double> cost= new TreeMap<String,Double>();
        Map<String,String> parent= new TreeMap<String,String>();
        cost.put(from,(double)0);
        int n=m.size();
        for(int i=0;i<n-1;i++)
        {
            String chosen="";
            double minn=100000000;
            for(String s:vertices)
            {
                if(vis.get(s)==null)
                {
                    if(cost.get(s)==null) continue;
                    if(cost.get(s)<minn)
                    {
                        minn=cost.get(s);
                        chosen=s;
                    }
                }
            }
            if(chosen.equals("")) continue;
            vis.put(chosen,1);
            LinkedList<Node> connected= new LinkedList<Node>();
            connected=m.get(chosen);
            for(Node x:connected)
            {
                double newCost= minn+x.getWt();
                if(cost.get(x.getName())==null)
                {
                    cost.put(x.getName(),newCost);
                    parent.put(x.getName(),chosen);
                }
                else 
                {
                    double oldCost= cost.get(x.getName());
                    if(oldCost>newCost)
                    {
                        cost.put(x.getName(),newCost);
                        parent.put(x.getName(),chosen);
                    }
                    else{}
                }
            } 
        }
        /////
        Stack<String> ans= new Stack<String>();
        if(cost.get(to)==null)
        {
            return new LinkedList<String>();
        }
        else
        {
            LinkedList<String> ll = new LinkedList<String>();
            ans.push(to);
            while(parent.get(to)!=null)
            {
                ans.push(parent.get(to));
                to=parent.get(to);
            }
            while(!ans.empty())
            {
                String x= ans.pop();
                ll.add(x);
            }
            return ll;
        }
    }
}

public class MouseGraph extends JFrame implements MouseListener, MouseMotionListener, ActionListener 
{
    int mode = 1;
    Map<String,Point> vertex;
    Map<String,LinkedList<Node>> graph;
    LinkedList<Node> edges;
    LinkedList<LinkedList<String>> path;
    
    Timer timer = new Timer(10,this);
    
    JButton mode1,mode2,mode3,mode4,djikstra;
    JLabel label1,label2,label3,label4;
    
    Point press=null,release=null,curr=null;
    String press_name=null,release_name=null;
    
    LinkedList<String> obj_from=new LinkedList<String>(), obj_to = new LinkedList<String>();
    LinkedList<Double> obj_x=new LinkedList<Double>(),obj_y=new LinkedList<Double>();
    LinkedList<Double> obj_currx=new LinkedList<Double>(),obj_curry=new LinkedList<Double>();
    LinkedList<Integer> obj_mode=new LinkedList<Integer>(),idx=new LinkedList<Integer>();
    
    MouseGraph()
    {
        vertex = new TreeMap<String,Point>();
        graph = new TreeMap<String,LinkedList<Node>>();
        edges = new LinkedList<Node>();
        path = new LinkedList<LinkedList<String>>();
        
        mode1 = new JButton("Add/Delete Vertex");
        mode1.setBounds(15,450,150,20);
        mode2 = new JButton("Add Edge");
        mode2.setBounds(15,480,150,20);
        mode3 = new JButton("Move Vertex");
        mode3.setBounds(15,510,150,20);
        mode4 = new JButton("Edit/Delete Edge");
        mode4.setBounds(15,540,150,20);
        djikstra = new JButton("Animate Path");
        djikstra.setBounds(400,450,150,20);
        JButton stop = new JButton("Stop Animation");
        stop.setBounds(400,480,150,20);
        add(stop);
        JButton importt = new JButton("Export Graph");
        importt.setBounds(400,510,150,20);
        add(importt);
        
        
        label1 = new JLabel("ON");
        label1.setForeground(Color.GREEN);
        label1.setBounds(200,450,150,20);
        add(label1);
        label2 = new JLabel("OFF");
        label2.setForeground(Color.RED);
        label2.setBounds(200,480,150,20);
        add(label2);
        label3 = new JLabel("OFF");
        label3.setForeground(Color.RED);
        label3.setBounds(200,510,150,20);
        add(label3);
        label4 = new JLabel("OFF");
        label4.setForeground(Color.RED);
        label4.setBounds(200,540,150,20);
        add(label4);
        
        JLabel modee = new JLabel("Mode");
        modee.setBounds(65,425,150,25);
        modee.setFont(new Font("Serif", Font.PLAIN, 20));
        add(modee);
        JLabel statuss = new JLabel("Status");
        statuss.setBounds(190,425,150,25);
        statuss.setFont(new Font("Serif", Font.PLAIN, 20));
        add(statuss);
        JLabel features = new JLabel("Features");
        features.setBounds(440,425,150,25);
        features.setFont(new Font("Serif", Font.PLAIN, 20));
        add(features);
        
        importt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                Comparator<Node> comp=new Comparator<Node>()
                {
                    public int compare(Node a, Node b)
                    {
                        if(a.getName().compareTo(b.getName())>0) return 1;
                        else return -1;
                    }
                };
                String path = JOptionPane.showInputDialog("Enter file path");
                PrintStream o=null;
                try 
                {
                    o = new PrintStream(new File(path));
                }
                catch(Exception e) 
                {
                    System.out.println(" Error \n");
                }
                System.setOut(o);

                int size=0;

                for(Map.Entry<String,Point> it:vertex.entrySet())
                {
                    size++;
                }
                System.out.println(size);
                for(Map.Entry<String,Point> it:vertex.entrySet())
                {
                    System.out.println(it.getKey()+" "+it.getValue().getX()+" "+it.getValue().getY());
                }
                size=0;
                for(Map.Entry<String,LinkedList<Node>> it:graph.entrySet())
                {
                    LinkedList<Node> l = it.getValue();
                    Collections.sort(l,comp);
                    for(Node x:l)
                    {
                        size++;
                    }
                }
                System.out.println(size);
                for(Map.Entry<String,LinkedList<Node>> it:graph.entrySet())
                {
                    LinkedList<Node> l = it.getValue();
                    Collections.sort(l,comp);
                    for(Node x:l)
                    {
                        System.out.println(it.getKey()+" "+x.getName()+" "+x.getWt());
                    }
                }
    
            }
        });
        
        mode1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                obj_mode.clear();
                mode = 1;                
                timer.stop(); 
                label1.setText("ON"); label1.setForeground(Color.GREEN);
                label2.setText("OFF"); label2.setForeground(Color.RED);
                label3.setText("OFF"); label3.setForeground(Color.RED);
                label4.setText("OFF"); label4.setForeground(Color.RED);
            }
        });
        mode2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                obj_mode.clear();
                mode = 2;                
                timer.stop(); 
                label1.setText("OFF"); label1.setForeground(Color.RED);
                label2.setText("ON"); label2.setForeground(Color.GREEN);
                label3.setText("OFF"); label3.setForeground(Color.RED);
                label4.setText("OFF"); label4.setForeground(Color.RED);
            }
        });
        mode3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                obj_mode.clear();
                mode = 3;                
                timer.stop(); 
                label1.setText("OFF"); label1.setForeground(Color.RED);
                label2.setText("OFF"); label2.setForeground(Color.RED);
                label3.setText("ON"); label3.setForeground(Color.GREEN);
                label4.setText("OFF"); label4.setForeground(Color.RED);
            }
        });
        mode4.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                obj_mode.clear();
                mode = 4;                
                timer.stop(); 
                label1.setText("OFF"); label1.setForeground(Color.RED);
                label2.setText("OFF"); label2.setForeground(Color.RED);
                label3.setText("OFF"); label3.setForeground(Color.RED);
                label4.setText("ON"); label4.setForeground(Color.GREEN);
            }
        });
        
        
        djikstra.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                mode=-1;
                
                label1.setText("OFF"); label1.setForeground(Color.RED);
                label2.setText("OFF"); label2.setForeground(Color.RED);
                label3.setText("OFF"); label3.setForeground(Color.RED);
                label4.setText("OFF"); label4.setForeground(Color.RED);
                               
                JFrame f1 = new JFrame("Input");
                
                JPanel p1 = new JPanel();
                p1.setLayout(new GridLayout(3,2));
                
                JLabel l1 = new JLabel("Enter from vertex: ");
                JLabel l2 = new JLabel("Enter to vertex: ");
                JTextField t1 = new JTextField();
                JTextField t2 = new JTextField();
                JButton b1 = new JButton("OK");
                JButton b2 = new JButton("Cancel");
                p1.add(l1); p1.add(t1); p1.add(l2); p1.add(t2); p1.add(b1); p1.add(b2);
                f1.add(p1);
                f1.setResizable(false);
                f1.setSize(250,100);
                f1.setVisible(true);
                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
                b2.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        f1.dispose();
                    }
                });
                
                b1.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        if((t1.getText()==null)||(t2.getText()==null))
                        {
                            Error.display("Enter all fields");
                            f1.dispose();
                        }
                        else if(!vertex.containsKey(t1.getText())||(!vertex.containsKey(t2.getText())))
                        {
                            Error.display("Enter valid vertices");
                            f1.dispose();
                        }
                        else
                        {
                            path.add(Dijkstra.solve(graph,t1.getText(),t2.getText()));
                            if(path.getLast().size()==0) { Error.display("No path exists"); f1.dispose(); }
                            else
                            {
                                f1.dispose();
                                idx.add(0);
                                obj_from.add(path.getLast().get(0));
                                obj_to.add(path.getLast().get((1)%path.getLast().size()));
                                
                                obj_currx.add((double)vertex.get(path.getLast().get(0)).getX()); 
                                obj_curry.add((double)vertex.get(path.getLast().get(0)).getY());
                                obj_x.add((double)((double)vertex.get(obj_to.getLast()).getX()-(double)vertex.get(obj_from.getLast()).getX())/(double)100);
                                obj_y.add((double)((double)vertex.get(obj_to.getLast()).getY()-(double)vertex.get(obj_from.getLast()).getY())/(double)100);
                                
                                JFrame fa = new JFrame();
                                JPanel pa = new JPanel();
                                JLabel la = new JLabel("Choose a shape");
                                pa.setLayout(new GridLayout(7,1));
                                JButton b1a = new JButton("Circle");
                                JButton b2a = new JButton("Plus");
                                JButton b3a = new JButton("Cross");
                                JButton b4a = new JButton("Triangle");
                                JButton b5a = new JButton("Square");
                                JButton b6a = new JButton("Cancel");
                                fa.add(pa);
                                pa.add(la);
                                pa.add(b1a);
                                pa.add(b2a);
                                pa.add(b3a);
                                pa.add(b4a);
                                pa.add(b5a);
                                pa.add(b6a);
                                
                                
                                fa.setResizable(false);
                                fa.setSize(80,250);
                                fa.setVisible(true);
                                fa.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                b1a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(1);
                                        fa.dispose();
                                        timer.start();
                                    }
                                });
                                b2a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(2);
                                        fa.dispose();
                                        timer.start();
                                    }
                                });
                                b3a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(3);
                                        fa.dispose();
                                        timer.start();
                                    }
                                });
                                b4a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(4);
                                        fa.dispose();
                                        timer.start();
                                    }
                                });
                                b5a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(5);
                                        fa.dispose();
                                        timer.start();
                                    }
                                });
                                b6a.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e)
                                    {
                                        obj_mode.add(-1);
                                        fa.dispose();
                                    }
                                });
                            }
                        }
                    }
                });                
                
            }
        });
        
        stop.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ee)
            {
                obj_from.clear();
                obj_to.clear();
                obj_x.clear();
                obj_y.clear();
                obj_currx.clear();
                obj_curry.clear();
                obj_mode.clear();
                path.clear();
                timer.stop();
                mode=-1;
                repaint();
            }
        });
        
        
        
        add(djikstra);
        add(mode1);
        add(mode2);
        add(mode3);
        add(mode4);
        
        setSize(900,600);       
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    
    
    
    
    public void paint(Graphics g)
    {
        super.paint(g);
        
        for(int i=0;i<obj_mode.size();i++)
        {
        
            if(obj_mode.get(i)==1)
            {
                g.setColor(Color.BLACK);
                g.fillOval(obj_currx.get(i).intValue(),(int)obj_curry.get(i).intValue(),15,15);

            }

            if(obj_mode.get(i)== 2)
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(Color.GREEN);
                g2.fill(Shapes.plus((int)obj_currx.get(i).intValue(),(int)obj_curry.get(i).intValue()));

            }

            if(obj_mode.get(i) == 4)
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(Color.PINK);
                g2.fill(Shapes.triangle((int)obj_currx.get(i).intValue(),(int)obj_curry.get(i).intValue()));

            }

            if(obj_mode.get(i) == 3)
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(Color.YELLOW);
                g2.fill(Shapes.cross((int)obj_currx.get(i).intValue(),(int)obj_curry.get(i).intValue()));
            }

            if(obj_mode.get(i) == 5)
            {
                g.setColor(Color.ORANGE);
                g.fillRect((int)(obj_currx.get(i).intValue()-7),(int)(obj_curry.get(i).intValue()-7),14,14);
            }
        }
        
        if(mode==2)
        {
            g.setColor(Color.BLUE);
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(3.0f));
            g2.setPaint(Color.BLUE);
            if(press!=null) g2.drawLine(press.getX(),press.getY(),curr.getX(),curr.getY());
        } 
        
        if(mode==3)
        {
            if(press_name!=null) vertex.put(press_name,curr);
        }
        
        g.setColor(Color.BLUE);
        for(Map.Entry<String,LinkedList<Node>> it:graph.entrySet())
        {
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(3.0f));
            
            LinkedList<Node> l = it.getValue();
            for(Node x:l)
            {
                g2.setPaint(Color.BLUE);
                g2.drawLine(vertex.get(it.getKey()).getX()+7,vertex.get(it.getKey()).getY()+7,vertex.get(x.getName()).getX()+7,vertex.get(x.getName()).getY()+7);
                g2.setColor(Color.BLACK);
                g2.drawString(Double.toString(x.getWt()),(float)(vertex.get(it.getKey()).getX()+7+vertex.get(x.getName()).getX()+7)/2f,(float)(vertex.get(it.getKey()).getY()+7+vertex.get(x.getName()).getY()+7)/2f);
            }
        }
        
        
        for(Map.Entry<String,Point> it:vertex.entrySet())
        {
            g.setColor(Color.RED);
            int x=it.getValue().getX();
            int y=it.getValue().getY();
            g.fillOval(x,y,15,15);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(Color.BLACK);
            g2d.drawString(it.getKey(),x,y);
        }       
        
        if(mode==3)
        {
            if(press_name!=null) vertex.remove(press_name);
        }
        
        
    };
    
    
    
    public void mouseClicked(MouseEvent e)
    {
        
    }    
    
    public void mousePressed(MouseEvent e)
    {
        if(mode==1)
        {
            press=null; release=null; curr=null;
            press_name=null; release_name=null;
            int flag=1;
            int X = e.getXOnScreen(), Y = e.getYOnScreen();
            String v="";
            for(Map.Entry<String,Point> it:vertex.entrySet())
            {
                int x=it.getValue().getX();
                int y=it.getValue().getY();
                if((x<=15+X&&x>=X-15)&&(y<=15+Y&&y>=Y-15)) 
                {
                    v=it.getKey();
                    flag=0;
                }
            }
            final String v1=v;
            if(flag==1)
            {
                Graphics g = getGraphics();
                g.setColor(Color.RED);
                String name = JOptionPane.showInputDialog("Enter vertex name");
                if(name!=null)
                {
                    if(vertex.containsKey(name)) Error.display("Name already used");
                    else
                    {
                        g.fillOval(e.getXOnScreen()-7,e.getYOnScreen()-7,15,15);
                        Graphics2D g2d = (Graphics2D)g;
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(name,e.getXOnScreen()-7,e.getYOnScreen()-7);
                        vertex.put(name,new Point(e.getXOnScreen()-7,e.getYOnScreen()-7));
                    }
                }
            }
            else
            {
                JFrame f = new JFrame("Delete");
                JPanel p = new JPanel();
                p.setLayout(new GridLayout(3,1));
                JLabel l = new JLabel("Delete Vertex ?");
                JButton b1 = new JButton("Yes");
                JButton b2 = new JButton("No");
                p.add(l);
                p.add(b1);
                p.add(b2);
                f.setSize(100,100);
                f.add(p);
                f.setVisible(true);
                f.setResizable(false);
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                b1.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        vertex.remove(v1);
                        graph.remove(v1);
                        Map<String,LinkedList<Node>> graph1 = new TreeMap<String,LinkedList<Node>>();
                        for(Map.Entry<String,LinkedList<Node>> it:graph.entrySet())
                        {
                            graph1.put(it.getKey(),new LinkedList<Node>());
                            for(Node x:it.getValue())
                            {
                                if(!x.getName().equals(v1)) graph1.get(it.getKey()).add(x);
                            }
                            graph = graph1;
                        }
                        repaint();
                        f.dispose();
                    }
                });
                b2.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        f.dispose();
                    }
                });
            }
        }
        
        if(mode==2)
        {
            press=null; release=null; curr=null;
            press_name=null; release_name=null;
            int X = e.getXOnScreen(), Y = e.getYOnScreen();
            for(Map.Entry<String,Point> it:vertex.entrySet())
            {
                int x=it.getValue().getX();
                int y=it.getValue().getY();
                if((x<=15+X&&x>=X-15)&&(y<=15+Y&&y>=Y-15)) 
                {
                    press_name=it.getKey();
                    press = new Point((int)x+7,(int)y+7);
                }
            }           
        }
        if(mode==3)
        {
            press=null; release=null; curr=null;
            press_name=null; release_name=null;
            int X = e.getXOnScreen(), Y = e.getYOnScreen();
            for(Map.Entry<String,Point> it:vertex.entrySet())
            {
                int x=it.getValue().getX();
                int y=it.getValue().getY();
                if((x<=15+X&&x>=X-15)&&(y<=15+Y&&y>=Y-15)) 
                {
                    press_name=it.getKey();
                    press = new Point((int)x,(int)y);
                }
            }
            if(press_name!=null)
            {
                edges.clear();
                edges = graph.get(press_name);
                graph.remove(press_name);
                vertex.remove(press_name);
                curr = press;
            }
        }
        
        if(mode==4)
        {
            Point B = new Point(e.getX(),e.getY());
            String from = null, to = null;
            for(Map.Entry<String,LinkedList<Node>> it:graph.entrySet())
            {
                for(Node x:it.getValue())
                {
                    if(Check.inLine(vertex.get(it.getKey()),B,vertex.get(x.getName())))
                    {
                        from = it.getKey();
                        to = x.getName();
                    }
                }
            }
            if(from!=null)
            {
                final String fromm = from, too =to;
                JFrame fx = new JFrame("Change details");
                JPanel px = new JPanel();
                px.setLayout(new GridLayout(3,1));
                JButton edit = new JButton("Edit Edge");
                JButton delete = new JButton("Delete Edge");
                JButton cancel = new JButton("Cancel");
                px.add(edit); px.add(delete); px.add(cancel);
                fx.add(px);
                fx.setSize(320,120);
                fx.setVisible(true);
                fx.setResizable(false);
                fx.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cancel.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        fx.dispose();
                    }
                });
                edit.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        try
                        {
                            fx.dispose();
                            String name = JOptionPane.showInputDialog("Enter new weight");
                            int wt = Integer.parseInt(name);
                            if(name!=null)
                            {

                                int c=0;
                                for(Node xx:graph.get(fromm))
                                {
                                    if(xx.getName().equals(too)) graph.get(fromm).remove(c);
                                    c++;
                                }
                                graph.get(fromm).add(new Node(too,Integer.parseInt(name)));
                                c=0;
                                for(Node xx:graph.get(too))
                                {
                                    if(xx.getName().equals(fromm)) graph.get(too).remove(c);
                                    c++;
                                }
                                graph.get(too).add(new Node(fromm,Integer.parseInt(name)));
                            }
                            repaint();
                        }
                        catch(NumberFormatException eeee)
                        {
                            Error.display("Enter valid integer");
                        }
                    }
                });
                delete.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent ee)
                    {
                        fx.dispose();
                        
                        int c=0;
                        for(Node xx:graph.get(fromm))
                        {
                            if(xx.getName().equals(too)) graph.get(fromm).remove(c);
                            c++;
                        }
                        c=0;
                        for(Node xx:graph.get(too))
                        {
                            if(xx.getName().equals(fromm)) graph.get(too).remove(c);
                            c++;
                        }


                        repaint();
                    }
                });
                
            }
        }
    }
    
    public void mouseDragged(MouseEvent e)
    {
        if(mode==2) 
        {
            curr = new Point(e.getX(),e.getY());
            repaint();
        }
        if(mode==3)
        {
            curr = new Point(e.getX(),e.getY());
            repaint();
        }
    }
    
    public void mouseReleased(MouseEvent e)
    {
        if(mode==2)
        {
            release = null;
            int X = e.getXOnScreen(), Y = e.getYOnScreen();
            for(Map.Entry<String,Point> it:vertex.entrySet())
            {
                int x=it.getValue().getX();
                int y=it.getValue().getY();
                if((x<=15+X&&x>=X-15)&&(y<=15+Y&&y>=Y-15)) 
                {
                    release_name=it.getKey();
                    release = new Point((int)x+7,(int)y+7);
                }
            }
            if(press!=null&&release!=null&&(release_name!=press_name))
            {
                if(!graph.containsKey(press_name))
                {
                    graph.put(press_name,new LinkedList<Node>());
                }
                if(!graph.containsKey(release_name))
                {
                    graph.put(release_name,new LinkedList<Node>());
                }
                int flag=1;
                for(Node x:graph.get(press_name))
                {
                    if(x.getName().equals(release_name))
                    {
                        Error.display("Edge already present");
                        flag=0;
                    }
                }
                if(flag==1)
                {
                    String name = JOptionPane.showInputDialog("Enter weight");
                    if(name!=null)
                    {
                        try
                        {
                            int wt = Integer.parseInt(name);
                            graph.get(press_name).add(new Node(release_name,wt));
                            graph.get(release_name).add(new Node(press_name,wt));
                        }
                        catch(NumberFormatException ee)
                        {
                            Error.display("Enter valid integer");
                        }
                    }
                }
            }
            press=null;
            repaint();
        }
        
        if(mode==3)
        {
            vertex.put(press_name,curr);
            graph.put(press_name,new LinkedList<Node>());
            for(Node x:edges)
            {
                graph.get(press_name).add(x);
            }
            press_name=null;
            repaint();
        }
    }  
    
    public void actionPerformed(ActionEvent e)
    {
        for(int i=0;i<obj_mode.size();i++)
        {
            if(obj_mode.get(i)!=-1)
            {
                obj_currx.set(i,obj_currx.get(i)+obj_x.get(i));
                obj_curry.set(i,obj_curry.get(i)+obj_y.get(i));
                
                if(Math.abs(obj_currx.get(i).intValue()-vertex.get(obj_to.get(i)).getX())<2&&Math.abs(obj_curry.get(i).intValue()-vertex.get(obj_to.get(i)).getY())<2)
                {
                    idx.set(i,idx.get(i)+1);
                    if(idx.get(i)==path.get(i).size()-1) idx.set(i,0);
                    obj_from.set(i,path.get(i).get((idx.get(i))%path.get(i).size()));
                    obj_to.set(i,path.get(i).get((idx.get(i)+1)%path.get(i).size()));
                    obj_currx.set(i,(double)vertex.get(obj_from.get(i)).getX());
                    obj_curry.set(i,(double)vertex.get(obj_from.get(i)).getY());
                    obj_x.set(i,(double)((double)vertex.get(obj_to.get(i)).getX()-(double)vertex.get(obj_from.get(i)).getX())/(double)100);
                    obj_y.set(i,(double)((double)vertex.get(obj_to.get(i)).getY()-(double)vertex.get(obj_from.get(i)).getY())/(double)100);
                }
            }
        }
        repaint();
    }
    
    public void mouseExited(MouseEvent e)
    {
        
    }
    
    public void mouseEntered(MouseEvent e)
    {
        
    }    
    
    public void mouseMoved(MouseEvent e)
    {
        
    }
   

    public static void main(String[] args) 
    {
        new MouseGraph();  
    }    
}
