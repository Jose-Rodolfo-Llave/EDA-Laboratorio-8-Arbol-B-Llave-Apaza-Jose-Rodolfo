import java.util.*;

public class BNodeGeneric <T extends Comparable<? super T>> {
	Vector <T> keys;
	int MinDeg;
	Vector <BNodeGeneric<T>> children;
	int num;
	boolean isLeaf;
	
	public BNodeGeneric(int deg, boolean isLeaf) {
		this.MinDeg = deg;
		this.isLeaf = isLeaf;
		this.keys = new Vector<T>();
		this.children = new Vector <BNodeGeneric<T>>();
		this.num = 0;
	}
	

    public BNodeGeneric <T> search(T key){
        int i = 0;
        while (i < num && (key.compareTo(keys.get(i)) > 0))
            i++;

        if (keys.get(i).compareTo(key) == 0)
            return this;
        if (isLeaf)
            return null;
        return children.get(i).search(key);
    }
    
    public void splitChild(int i ,BNodeGeneric <T> y){

      
    	BNodeGeneric <T> z = new BNodeGeneric <T>(y.MinDeg,y.isLeaf);
        z.num = MinDeg - 1;

       
        for (int j = 0; j < MinDeg-1; j++)
        	z.keys.set(j, y.keys.get(j + MinDeg));
        if (!y.isLeaf){
            for (int j = 0; j < MinDeg; j++)
            	z.children.set(j, y.children.get(j + MinDeg));
        }
        y.num = MinDeg-1;

        
        for (int j = num; j >= i+1; j--)
            children.set(j + 1, children.get(j));
        
        children.set(i + 1, z);

       
        for (int j = num-1;j >= i;j--)
        	keys.set(j + 1, keys.get(j));
        keys.set(i, y.keys.get(MinDeg - 1));

        num = num + 1;
    }
    
    public void insertNotFull(T key){

        int i = num -1; 

        if (isLeaf){ 
          
            while (i >= 0 && keys.get(i).compareTo(key) > 0){
            	keys.set(i + 1, keys.get(i));
                i--;
            }
            keys.set(i + 1, key);
            num = num +1;
        }
        else{
           
            while (i >= 0 && keys.get(i).compareTo(key) > 0)
                i--;
            if (children.get(i + 1).num == 2*MinDeg - 1){ 
                splitChild(i+1,children.get(i + 1));
             
                if (keys.get(i + 1).compareTo(key) < 0)
                    i++;
            }
            children.get(i + 1).insertNotFull(key);
        }
    }
    
    public void remove(T key){

        int idx = findKey(key);
        if (idx < num && keys.get(idx).compareTo(key) == 0){ 
            if (isLeaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx);
        }
        else{
            if (isLeaf){ 
                System.out.printf("The key %d is does not exist in the tree\n",key);
                return;
            }

            boolean flag = idx == num; 
            
            if (children.get(idx).num < MinDeg) 
                fill(idx);
       
            if (flag && idx > num)
                children.get(idx - 1).remove(key);
            else
            	children.get(idx).remove(key);
        }
    }
    
    public int findKey(T key){

        int idx = 0;
        
        while (idx < num && keys.get(idx).compareTo(key) < 0)
            ++idx;
        return idx;
    }
    
    public void removeFromLeaf(int idx){
      
        for (int i = idx +1;i < num;++i)
        	keys.set(i - 1, keys.get(i));
        num --;
    }
    
    public void removeFromNonLeaf(int idx){

        T key = keys.get(idx);

        if (children.get(idx).num >= MinDeg){
            T pred = getPred(idx);
            keys.set(idx, pred);
            children.get(idx).remove(pred);
        }

        else if (children.get(idx + 1).num >= MinDeg){
            T succ = getSucc(idx);
            keys.set(idx, succ);
            children.get(idx + 1).remove(succ);
        }
        else{
            merge(idx);
            children.get(idx).remove(key);
        }
    }
    
    public T getPred(int idx){ 
        BNodeGeneric<T> cur = children.get(idx);
        while (!cur.isLeaf)
        	cur = cur.children.get(cur.num);
        return cur.keys.get(cur.num - 1);
    }
    
    public T getSucc(int idx){ 

    	BNodeGeneric<T> cur = children.get(idx);
        while (!cur.isLeaf)
            cur = cur.children.get(0);
        return cur.keys.get(0);
    }
    
    public void fill(int idx){

        if (idx != 0 && children.get(idx - 1).num >= MinDeg)
            borrowFromPrev(idx);
        else if (idx != 0 && children.get(idx + 1).num >= MinDeg)
            borrowFromNext(idx);
        else{
            if (idx != num)
                merge(idx);
            else
                merge(idx-1);
        }
    }
    
    public void borrowFromPrev(int idx){

        BNodeGeneric<T> child = children.get(idx);
        BNodeGeneric<T> sibling = children.get(idx - 1);

        for (int i = child.num-1; i >= 0; --i) 
            child.keys.set(i + 1, child.keys.get(i));

        if (!child.isLeaf){ 
            for (int i = child.num; i >= 0; --i)
            	child.children.set(i + 1, child.children.get(i));
        }

        child.keys.set(0, keys.get(idx - 1));
        if (!child.isLeaf) 
        	child.children.set(0, sibling.children.get(sibling.num));

        keys.set(idx - 1, sibling.keys.get(sibling.num - 1));
        child.num += 1;
        sibling.num -= 1;
    }
    
    public void borrowFromNext(int idx){

        BNodeGeneric<T> child = children.get(idx);
        BNodeGeneric<T> sibling = children.get(idx + 1);
        
        child.keys.set(child.num, keys.get(idx));

        if (!child.isLeaf)
        	child.children.set(child.num + 1, sibling.children.get(0));

        keys.set(idx, sibling.keys.get(0));
        for (int i = 1; i < sibling.num; ++i)
        	sibling.keys.set(i - 1, sibling.keys.get(i));

        if (!sibling.isLeaf){
            for (int i= 1; i <= sibling.num;++i)
            	sibling.children.set(i - 1, sibling.children.get(i));
        }
        child.num += 1;
        sibling.num -= 1;
    }
    
    public void merge(int idx){

    	BNodeGeneric<T> child = children.get(idx);
        BNodeGeneric<T> sibling = children.get(idx + 1);
    
        child.keys.set(MinDeg - 1, keys.get(idx));

        for (int i =0 ; i< sibling.num; ++i)
        	child.keys.set(i + MinDeg, sibling.keys.get(i));

        if (!child.isLeaf){
            for (int i = 0;i <= sibling.num; ++i)
            	child.children.set(i + MinDeg, sibling.children.get(i));
        }

        for (int i = idx+1; i<num; ++i)
        	keys.set(i - 1, keys.get(i));

        for (int i = idx+2;i<=num;++i)
        	children.set(i - 1, children.get(i));

        child.num += sibling.num + 1;
        num--;
    }

}
