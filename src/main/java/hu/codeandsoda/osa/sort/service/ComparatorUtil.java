package hu.codeandsoda.osa.sort.service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.RuleBasedCollator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ComparatorUtil {

    private static final String COLLATOR_RULES_HUN = (" < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T \n"
            + " < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS");

    private static Logger logger = LoggerFactory.getLogger(ComparatorUtil.class);

    public static RuleBasedCollator getHungarianCollator() {
        RuleBasedCollator hunCollator = null;
        try {
            hunCollator = new RuleBasedCollator(COLLATOR_RULES_HUN);
        } catch (ParseException e) {
            String errorLog = MessageFormat.format("action=parseCollatorRule, status=failed, collator=HUNGARIAN ,error={0}", e.getMessage());
            logger.error(errorLog, e);
        }
        return hunCollator;
    }

}
