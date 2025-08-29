package com.example.webhooksqlsolver.service;

import org.springframework.stereotype.Service;

@Service
public class SqlProblemSolver {
    
    /**
     * Solves SQL problem based on the question type determined by regNo
     * @param regNo Registration number to determine question type
     * @return SQL query solution
     */
    public String solveSqlProblem(String regNo) {
        int lastTwoDigits = getLastTwoDigits(regNo);
        boolean isOdd = lastTwoDigits % 2 == 1;
        
        if (isOdd) {
            return solveQuestion1();
        } else {
            return solveQuestion2();
        }
    }
    
    private int getLastTwoDigits(String regNo) {
        if (regNo == null || regNo.length() < 2) {
            return 0;
        }
        
        // Extract last two characters and convert to number
        String lastTwo = regNo.substring(regNo.length() - 2);
        try {
            return Integer.parseInt(lastTwo);
        } catch (NumberFormatException e) {
            // If last two characters are not numeric, use ASCII values
            return (int) lastTwo.charAt(0) + (int) lastTwo.charAt(1);
        }
    }
    
    /**
     * Solution for Question 1 (Odd regNo)
     * Common SQL problems for odd numbers typically involve:
     * - Employee/Department queries
     * - Join operations
     * - Aggregations
     */
    private String solveQuestion1() {
        // This is a template solution for Question 1
        // Replace with actual SQL based on the specific problem from the Google Drive link
        return """
            SELECT 
                e.employee_id,
                e.employee_name,
                d.department_name,
                e.salary
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.department_id
            WHERE e.salary > (
                SELECT AVG(salary) 
                FROM employees 
                WHERE department_id = e.department_id
            )
            ORDER BY e.salary DESC;
            """;
    }
    
    /**
     * Solution for Question 2 (Even regNo)
     * Common SQL problems for even numbers typically involve:
     * - Customer/Order queries
     * - Window functions
     * - Complex joins
     */
    private String solveQuestion2() {
        // This is a template solution for Question 2
        // Replace with actual SQL based on the specific problem from the Google Drive link
        return """
            SELECT 
                c.customer_id,
                c.customer_name,
                COUNT(o.order_id) as total_orders,
                SUM(o.order_amount) as total_spent,
                RANK() OVER (ORDER BY SUM(o.order_amount) DESC) as spending_rank
            FROM customers c
            LEFT JOIN orders o ON c.customer_id = o.customer_id
            WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            GROUP BY c.customer_id, c.customer_name
            HAVING COUNT(o.order_id) >= 5
            ORDER BY total_spent DESC;
            """;
    }
}