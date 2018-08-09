/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simula.runtime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 * <pre>
 * link class ShapeElement;
 * begin 
 * 
 * 
 *    procedure drawEllipse(x,y,width,height); long real x,y,width,height; ... ... ;
 *    procedure fillEllipse(x,y,width,height); long real x,y,width,height; ... ... ;
 * end;
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ShapeElement$ extends Link$ implements Animation$.Animable {
	Animation$ animation;
    Color drawColor; // null: no drawing
    Color fillColor; // null: no filling
    Stroke stroke;
    Shape shape;

    public void drawLine(double x1,double y1,double x2,double y2)
    { shape=new Line2D.Double(x1,y1,x2,y2);
      drawColor=animation.currentDrawColor;
      animation.repaintMe();
    }

    public void drawEllipse(double x,double y,double width,double height)
    { shape=new Ellipse2D.Double(x,y,width,height);
      drawColor=animation.currentDrawColor;
      animation.repaintMe();
    }

    public void drawRectangle(double x,double y,double width,double height)
    { shape=new Rectangle2D.Double(x,y,width,height);
      drawColor=animation.currentDrawColor;
      animation.repaintMe();
    }

    public void drawRoundRectangle(double x,double y,double width,double height, double arcw, double arch)
    { shape=new RoundRectangle2D.Double(x,y,width,height,arcw,arch);
      drawColor=animation.currentDrawColor;
      animation.repaintMe();
    }

    public void fillEllipse(double x,double y,double width,double height)
    { shape=new Ellipse2D.Double(x,y,width,height);
      fillColor=animation.currentFillColor;
      animation.repaintMe();
    }

    public void fillRectangle(double x,double y,double width,double height)
    { shape=new Rectangle2D.Double(x,y,width,height);
      fillColor=animation.currentFillColor;
      animation.repaintMe();
    }

    public void fillRoundRectangle(double x,double y,double width,double height, double arcw, double arch)
    { shape=new RoundRectangle2D.Double(x,y,width,height,arcw,arch);
      fillColor=animation.currentFillColor;
      animation.repaintMe();
    }
    
    public void instantMoveTo(double x,double y)
    { if(shape instanceof RectangularShape)
      { RectangularShape rect=((RectangularShape)shape);
        //System.out.println("Move "+shape.getClass().getSimpleName()+" to x="+x+", y="+y);
        rect.setFrame(x,y,rect.getWidth(),rect.getHeight());
      }
      animation.repaintMe();
    }
    
    // speed = pixels per milli-second ???
    public void moveTo(double x,double y,double speed)
    { Rectangle2D bnd=shape.getBounds2D();
      double x1=bnd.getX();
      double y1=bnd.getY();
      double dx=(x-x1)/500;
      double dy=(y-y1)/500;
      int wait=(int)speed/100;
      if(wait<1) wait=1;
      if(wait>50) wait=50;
      for(int i=0;i<500;i++)
      { x1=x1+dx; y1=y1+dy;
        instantMoveTo(x1,y1);
        try {Thread.sleep(wait);}catch(Exception e) {}
      }
      instantMoveTo(x,y);
    }

	// Constructor
	public ShapeElement$(RTObject$ staticLink) {
		super(staticLink);
		TRACE_BEGIN_DCL$("ShapeElement$");
		animation=(Animation$)staticLink;
		CODE$ = new ClassBody(CODE$, this,2) {
			public void STM() {
				TRACE_BEGIN_STM$("ShapeElement$",inner);
				ShapeElement$.this.stroke=((Animation$)(staticLink)).currentStroke;
			    into(((Animation$)(staticLink)).SQS);
				if (inner != null) inner.STM();
				TRACE_END_STM$("ShapeElement$");
			}
		};
	}

	public ShapeElement$ STM() {
		return ((ShapeElement$) CODE$.EXEC$());
	}

	public ShapeElement$ START() {
		START(this);
		return (this);
	}

    public void paint(Graphics2D g)
    { g.setStroke(stroke);
  	  if(fillColor!=null)	{ g.setColor(fillColor); g.fill(shape); }
      if(drawColor!=null)	{ g.setColor(drawColor); g.draw(shape); }
    }

}
