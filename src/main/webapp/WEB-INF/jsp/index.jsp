<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2">系統公告</h1>
        <div class="btn-toolbar mb-2 mb-md-0">
          <div class="btn-group me-2">
          <form name="postTest" method="POST" action="emp/query/RequestBody"> 
		    id: <input type="text" id="id" name="id"/>
		    pwd: <input type="text" id="pwd" name="pwd"/>
            <button type="submit" class="btn btn-sm btn-outline-secondary">RequestBody 測試</button>
		</form>
            <button type="button" class="btn btn-sm btn-outline-secondary">匯出 PDF</button>
          </div>
        </div>
      </div>
