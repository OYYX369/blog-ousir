package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.yupi.mianshiya.model.entity.QuestionBankQuestion;
import com.yupi.mianshiya.model.vo.QuestionBankQuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目表服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankquestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankquestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankquestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankquestionQueryRequest);
    
    /**
     * 获取题库题目表封装
     *
     * @param questionBankquestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankquestion, HttpServletRequest request);

    /**
     * 分页获取题库题目表封装
     *
     * @param questionBankquestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankquestionPage, HttpServletRequest request);
}
