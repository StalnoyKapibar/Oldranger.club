package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {
    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
    public List<ArticleTagsNode> findAll() {
        return tagsNodeRepository.findAll();
    }

    @Override
    public List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        List<ArticleTagsNodeDto> dto  = new ArrayList<>();
        for (Tuple i : tagsNodeRepository.findAllChildrenTree()) {
            dto.add(new ArticleTagsNodeDto(
                    i.get("id", Long.class),
                    i.get("parent", Long.class),
                    i.get("tag_name", String.class),
                    i.get("tags_hierarchy", String.class)));
            System.out.println(i);
        }
        return dto;

//        return tagsNodeRepository.findAllChildrenTree().stream()
//                .map(e -> new ArticleTagsNodeDto(e.getElements() tu ali.getId(),
//                        e.getParent() == null ? -1L : e.getParent(),
//                        e.getTag(),
//                        Arrays.stream(e.getPosition().toString().split(",")).mapToInt(Integer::parseInt).toArray().toString()))
//                .collect(Collectors.toList());

//        return tagsNodeRepository.findAllChildrenTree();

//        return tagsNodeRepository.findAllChildrenTree().stream()
//                .map(e -> new ArticleTagsNodeDto(
//                        (BigInteger) e .get(0),
//                        (BigInteger)e.get(1),
//                        (BigInteger)e.get(2),
//                        (BigInteger) e.get(3))).collect(Collectors.toList());

//        return tagsNodeRepository.findAllChildrenTree().stream()
//                .map(e -> new ArticleTagsNodeDto(
//                        (BigInteger) e .get(0),
//                        (ArticleTagsNode)e.get(1),
//                        (Integer)e.get(2),
//                        (BigInteger) e.get(3),
//                        (String) e.get(4))).collect(Collectors.toList());

        //        return tagsNodeRepository.findAllChildrenTree();

//        return tagsNodeRepository.findAllChildrenTree();


     }

    @Override
    public ArticleTagsNode findById(Long id) {
        return tagsNodeRepository.findById(id).orElse(null);
    }

    @Override
    public void save(ArticleTagsNode tagsNode) {
        tagsNodeRepository.save(tagsNode);
    }

    @Override
    public void deleteById(Long id) {
        tagsNodeRepository.deleteByIdIn(tagsNodeRepository.findDescendantsAndParentIdsByParentId(id));
    }
}
